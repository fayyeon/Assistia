package com.fayyeon.assistia.data

object QuickToggleRepository {

    val allToggles: List<RemapAction> = listOf(
        RemapAction(
            id = "toggle_auto_rotate",
            type = ActionType.QUICK_TOGGLE,
            title = "Auto-Rotate",
            subtitle = "",
            toggleKey = "auto_rotate",
            requiresPermission = "android.permission.WRITE_SETTINGS",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_battery_saver",
            type = ActionType.QUICK_TOGGLE,
            title = "Battery Saver",
            subtitle = "Requires ADB to work properly",
            toggleKey = "battery_saver",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_color_inversion",
            type = ActionType.QUICK_TOGGLE,
            title = "Color Inversion",
            subtitle = "Requires ADB to work properly",
            toggleKey = "color_inversion",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_dnd",
            type = ActionType.QUICK_TOGGLE,
            title = "Do Not Disturb",
            subtitle = "",
            toggleKey = "dnd",
            requiresPermission = "android.permission.ACCESS_NOTIFICATION_POLICY",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_eye_protection",
            type = ActionType.QUICK_TOGGLE,
            title = "Night Light",
            subtitle = "Requires ADB to work properly",
            toggleKey = "eye_protection",
            requiresAdb = true,
            isNonRootDirect = false
        ),
        RemapAction(
            id = "toggle_flashlight",
            type = ActionType.QUICK_TOGGLE,
            title = "Flashlight",
            subtitle = "",
            toggleKey = "flashlight",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_mobile_data",
            type = ActionType.QUICK_TOGGLE,
            title = "Mobile Data",
            subtitle = "",
            toggleKey = "mobile_data",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_sound_mode",
            type = ActionType.QUICK_TOGGLE,
            title = "Sound mode (Ring / Vibrate)",
            subtitle = "",
            toggleKey = "sound_mode",
            isNonRootDirect = true
        ),
        RemapAction(
            id = "toggle_wifi",
            type = ActionType.QUICK_TOGGLE,
            title = "Wi-Fi",
            subtitle = "",
            toggleKey = "wifi",
            isNonRootDirect = true
        )
    ).sortedBy { it.title }
}
