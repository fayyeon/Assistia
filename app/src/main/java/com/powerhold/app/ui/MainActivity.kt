package com.powerhold.app.ui

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.powerhold.app.ui.screens.MainDashboardScreen
import com.powerhold.app.ui.theme.PowerholdTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val assistantRoleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Powerhold set as Assistant!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val isAmoledMode by viewModel.isAmoledMode.collectAsState()

            PowerholdTheme(
                darkTheme = isSystemInDarkTheme(),
                amoledMode = isAmoledMode
            ) {
                MainDashboardScreen(
                    viewModel = viewModel,
                    onOpenAssistantSettings = { requestDefaultAssistant() }
                )
            }
        }
    }

    private fun requestDefaultAssistant() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_ASSISTANT)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT)
                    try {
                        assistantRoleLauncher.launch(intent)
                        return
                    } catch (e: Exception) {
                        // Fall back to settings
                    }
                } else {
                    Toast.makeText(this, "Powerhold is already your default assistant", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        // Fallbacks for MIUI / older Android or if role dialog is bypassed
        try {
            val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(this, "Go to Settings -> Apps -> Default apps -> Digital assistant", Toast.LENGTH_LONG).show()
            }
        }
    }
}
