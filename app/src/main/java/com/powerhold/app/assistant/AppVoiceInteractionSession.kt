package com.powerhold.app.assistant

import android.content.Context
import android.os.Bundle
import android.service.voice.VoiceInteractionSession

/**
 * Modern Android Assist Session triggered by power button hold, diagonal corner swipe,
 * or voice trigger when Powerhold is set as the default assistant.
 */
class AppVoiceInteractionSession(context: Context) : VoiceInteractionSession(context) {

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)

        // Execute mapped action and hide the session overlay immediately
        ActionExecutor.execute(context) {
            hide()
        }
    }
}
