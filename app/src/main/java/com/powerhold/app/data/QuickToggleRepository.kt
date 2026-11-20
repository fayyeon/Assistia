package com.powerhold.app.data

object QuickToggleRepository {

    val allToggles: List<RemapAction> = listOf(
        RemapAction(
            id = "toggle_airplane_mode",
            type = ActionType.QUICK_TOGGLE,
            title = "Airplane Mode",
            subtitle = "Opens Airplane Mode quick panel / settings (or direct with ADB)",
            toggleKey = "airplane_mode",
            requiresAdb = false,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_auto_rotate",
            type = ActionType.QUICK_TOGGLE,
            title = "Auto-Rotate Toggle",
            subtitle = "Directly toggles screen rotation lock (requires Write Settings permission)",
            toggleKey = "auto_rotate",
            requiresPermission = "android.permission.WRITE_SETTINGS",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_battery_saver",
            type = ActionType.QUICK_TOGGLE,
            title = "Battery Saver Toggle",
            subtitle = "Direct toggle with WRITE_SECURE_SETTINGS (ADB) or opens Battery settings",
            toggleKey = "battery_saver",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_bluetooth",
            type = ActionType.QUICK_TOGGLE,
            title = "Bluetooth Quick Panel",
            subtitle = "Opens native floating Bluetooth control panel",
            toggleKey = "bluetooth",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_camera_access",
            type = ActionType.QUICK_TOGGLE,
            title = "Camera Access (Sensor Privacy)",
            subtitle = "Opens Privacy Dashboard or toggles hardware sensor privacy",
            toggleKey = "camera_access",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_color_inversion",
            type = ActionType.QUICK_TOGGLE,
            title = "Color Inversion Toggle",
            subtitle = "Direct toggle with WRITE_SECURE_SETTINGS (ADB) or opens Accessibility",
            toggleKey = "color_inversion",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_data_saver",
            type = ActionType.QUICK_TOGGLE,
            title = "Data Saver Settings",
            subtitle = "Opens system Data Saver control screen",
            toggleKey = "data_saver",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_dnd",
            type = ActionType.QUICK_TOGGLE,
            title = "Do Not Disturb (DND) Toggle",
            subtitle = "Directly toggles DND Priority Mode (requires DND policy permission)",
            toggleKey = "dnd",
            requiresPermission = "android.permission.ACCESS_NOTIFICATION_POLICY",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_extra_dim",
            type = ActionType.QUICK_TOGGLE,
            title = "Extra Dim Toggle",
            subtitle = "Direct toggle with WRITE_SECURE_SETTINGS (ADB) or opens Display settings",
            toggleKey = "extra_dim",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_eye_protection",
            type = ActionType.QUICK_TOGGLE,
            title = "Eye Protection / Night Light",
            subtitle = "Direct toggle with WRITE_SECURE_SETTINGS (ADB) or opens Night Light",
            toggleKey = "eye_protection",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_flashlight",
            type = ActionType.QUICK_TOGGLE,
            title = "Flashlight Toggle",
            subtitle = "Direct 100% non-root instant torch on/off toggle",
            toggleKey = "flashlight",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_location",
            type = ActionType.QUICK_TOGGLE,
            title = "Location Toggle / Panel",
            subtitle = "Opens Location settings or toggles mode with ADB",
            toggleKey = "location",
            requiresAdb = false,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_microphone_access",
            type = ActionType.QUICK_TOGGLE,
            title = "Microphone Access (Sensor Privacy)",
            subtitle = "Opens Privacy Dashboard or toggles hardware sensor privacy",
            toggleKey = "microphone_access",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_mobile_data",
            type = ActionType.QUICK_TOGGLE,
            title = "Mobile Data Panel",
            subtitle = "Opens native floating Internet / Cellular panel",
            toggleKey = "mobile_data",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_sound_mode",
            type = ActionType.QUICK_TOGGLE,
            title = "Sound Mode Toggle (Ring / Vibrate / Silent)",
            subtitle = "Direct 100% non-root cycling of audio ringer mode",
            toggleKey = "sound_mode",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_wifi",
            type = ActionType.QUICK_TOGGLE,
            title = "Wi-Fi Panel",
            subtitle = "Opens native floating Internet / Wi-Fi control panel",
            toggleKey = "wifi",
            isNonRootDirect = true
        )
    ).sortedBy { it.title }
}
