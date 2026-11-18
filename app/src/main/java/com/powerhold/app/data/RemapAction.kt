package com.powerhold.app.data

/**
 * Represents any action that can be mapped to the assistant gesture trigger.
 */
data class RemapAction(
    val id: String,
    val type: ActionType,
    val title: String,
    val subtitle: String,
    val packageName: String? = null,
    val activityName: String? = null,
    val toggleKey: String? = null,
    val requiresAdb: Boolean = false,
    val requiresPermission: String? = null,
    val isNonRootDirect: Boolean = true
) {
    companion object {
        val DEFAULT = RemapAction(
            id = "toggle_flashlight",
            type = ActionType.QUICK_TOGGLE,
            title = "Flashlight Toggle",
            subtitle = "Turn torch on or off instantly without screen",
            toggleKey = "flashlight",
            isNonRootDirect = true
        )
    }
}
