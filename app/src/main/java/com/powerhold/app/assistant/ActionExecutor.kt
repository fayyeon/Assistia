package com.powerhold.app.assistant

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.Toast
import com.powerhold.app.data.ActionType
import com.powerhold.app.data.PreferencesManager
import com.powerhold.app.data.RemapAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ActionExecutor {

    fun execute(context: Context, onComplete: () -> Unit = {}) {
        val prefs = PreferencesManager(context)
        CoroutineScope(Dispatchers.Main).launch {
            val action = prefs.getSelectedAction()
            triggerHaptic(context)
            runAction(context, action, prefs)
            onComplete()
        }
    }

    private suspend fun runAction(context: Context, action: RemapAction, prefs: PreferencesManager) {
        when (action.type) {
            ActionType.OPEN_APP -> {
                action.packageName?.let { pkg ->
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        showToast(context, "Opening ${action.title}")
                    } else {
                        showToast(context, "Cannot launch ${action.title}")
                    }
                }
            }
            ActionType.LAUNCH_ACTIVITY -> {
                if (action.packageName != null && action.activityName != null) {
                    try {
                        val intent = Intent().apply {
                            component = ComponentName(action.packageName, action.activityName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        showToast(context, "Launched: ${action.title}")
                    } catch (e: Exception) {
                        showToast(context, "Failed to launch activity: ${e.localizedMessage}")
                    }
                }
            }
            ActionType.QUICK_TOGGLE -> {
                executeQuickToggle(context, action, prefs)
            }
        }
    }

    private suspend fun executeQuickToggle(context: Context, action: RemapAction, prefs: PreferencesManager) {
        when (action.toggleKey) {
            "flashlight" -> toggleTorch(context, prefs)
            "auto_rotate" -> toggleAutoRotate(context)
            "dnd" -> toggleDnd(context)
            "sound_mode" -> toggleSoundMode(context)
            "battery_saver" -> toggleBatterySaver(context)
            "color_inversion" -> toggleSecureSetting(
                context = context,
                settingKey = "accessibility_display_inversion_enabled",
                isGlobal = false,
                friendlyName = "Color Inversion",
                fallbackIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            )
            "extra_dim" -> toggleSecureSetting(
                context = context,
                settingKey = "reduce_bright_colors_activated",
                isGlobal = false,
                friendlyName = "Extra Dim",
                fallbackIntent = Intent(Settings.ACTION_DISPLAY_SETTINGS)
            )
            "eye_protection" -> toggleSecureSetting(
                context = context,
                settingKey = "night_display_activated",
                isGlobal = false,
                friendlyName = "Eye Protection (Night Light)",
                fallbackIntent = Intent(Settings.ACTION_NIGHT_DISPLAY_SETTINGS)
            )
            "bluetooth" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    openIntentSafely(context, Intent("android.settings.panel.action.BLUETOOTH"))
                } else {
                    openIntentSafely(context, Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }
                showToast(context, "Bluetooth Panel opened")
            }
            "wifi" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    openIntentSafely(context, Intent("android.settings.panel.action.INTERNET_CONNECTIVITY"))
                } else {
                    openIntentSafely(context, Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                showToast(context, "Wi-Fi Panel opened")
            }
            "mobile_data" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    openIntentSafely(context, Intent("android.settings.panel.action.INTERNET_CONNECTIVITY"))
                } else {
                    openIntentSafely(context, Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                }
                showToast(context, "Mobile Data Panel opened")
            }
            "airplane_mode" -> {
                openIntentSafely(context, Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
                showToast(context, "Airplane Mode settings opened")
            }
            "data_saver" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    openIntentSafely(context, Intent(Settings.ACTION_DATA_SAVER_SETTINGS))
                } else {
                    openIntentSafely(context, Intent(Settings.ACTION_SETTINGS))
                }
                showToast(context, "Data Saver settings opened")
            }
            "location" -> {
                openIntentSafely(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                showToast(context, "Location settings opened")
            }
            "camera_access", "microphone_access" -> {
                openIntentSafely(context, Intent(Settings.ACTION_PRIVACY_SETTINGS))
                showToast(context, "Privacy settings opened")
            }
            else -> {
                showToast(context, "Triggered: ${action.title}")
            }
        }
    }

    private suspend fun toggleTorch(context: Context, prefs: PreferencesManager) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        if (cameraManager == null) {
            showToast(context, "Camera service not available")
            return
        }

        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }

            if (cameraId != null) {
                val currentState = prefs.getTorchState()
                val newState = !currentState
                cameraManager.setTorchMode(cameraId, newState)
                prefs.setTorchState(newState)
                showToast(context, if (newState) "🔦 Flashlight ON" else "🔦 Flashlight OFF")
            } else {
                showToast(context, "No camera flash found")
            }
        } catch (e: CameraAccessException) {
            showToast(context, "Flashlight error: ${e.localizedMessage}")
        } catch (e: Exception) {
            showToast(context, "Error toggling flashlight: ${e.localizedMessage}")
        }
    }

    private fun toggleAutoRotate(context: Context) {
        if (!Settings.System.canWrite(context)) {
            showToast(context, "Grant 'Modify system settings' permission")
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        val current = Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            0
        )
        val next = if (current == 1) 0 else 1
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            next
        )
        showToast(context, if (next == 1) "🔄 Auto-rotate ON" else "🔒 Auto-rotate LOCKED")
    }

    private fun toggleDnd(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notificationManager == null) {
            showToast(context, "Notification service unavailable")
            return
        }

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            showToast(context, "Grant DND Policy access")
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        val currentFilter = notificationManager.currentInterruptionFilter
        val nextFilter = if (currentFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
            NotificationManager.INTERRUPTION_FILTER_PRIORITY
        } else {
            NotificationManager.INTERRUPTION_FILTER_ALL
        }

        notificationManager.setInterruptionFilter(nextFilter)
        showToast(
            context,
            if (nextFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY) "🌙 Do Not Disturb ON" else "🔔 Do Not Disturb OFF"
        )
    }

    private fun toggleSoundMode(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager == null) {
            showToast(context, "Audio service unavailable")
            return
        }

        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                showToast(context, "📳 Sound: Vibrate")
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                showToast(context, "🔇 Sound: Silent")
            }
            else -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                showToast(context, "🔔 Sound: Normal")
            }
        }
    }

    private fun toggleBatterySaver(context: Context) {
        try {
            val current = Settings.Global.getInt(context.contentResolver, "low_power", 0)
            val next = if (current == 1) 0 else 1
            Settings.Global.putInt(context.contentResolver, "low_power", next)
            showToast(context, if (next == 1) "🔋 Battery Saver ON" else "🔋 Battery Saver OFF")
        } catch (e: SecurityException) {
            // Needs WRITE_SECURE_SETTINGS via ADB, fallback to settings screen
            openIntentSafely(context, Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
            showToast(context, "Opened Battery Saver (Run ADB command for direct toggle)")
        }
    }

    private fun toggleSecureSetting(
        context: Context,
        settingKey: String,
        isGlobal: Boolean,
        friendlyName: String,
        fallbackIntent: Intent
    ) {
        try {
            val current = if (isGlobal) {
                Settings.Global.getInt(context.contentResolver, settingKey, 0)
            } else {
                Settings.Secure.getInt(context.contentResolver, settingKey, 0)
            }
            val next = if (current == 1) 0 else 1
            if (isGlobal) {
                Settings.Global.putInt(context.contentResolver, settingKey, next)
            } else {
                Settings.Secure.putInt(context.contentResolver, settingKey, next)
            }
            showToast(context, "$friendlyName: ${if (next == 1) "ON" else "OFF"}")
        } catch (e: SecurityException) {
            openIntentSafely(context, fallbackIntent)
            showToast(context, "Opened $friendlyName (Run ADB command for direct toggle)")
        }
    }

    private fun openIntentSafely(context: Context, intent: Intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast(context, "Unable to open screen: ${e.localizedMessage}")
        }
    }

    private fun triggerHaptic(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
        } catch (_: Exception) {}
    }

    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
}
