package com.fayyeon.assistia.assistant

import android.service.voice.VoiceInteractionService

/**
 * Android VoiceInteractionService that allows Assistia to be chosen as the
 * system-wide "Default Digital Assistant App" under Android Settings -> Apps -> Default Apps.
 */
class AppVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
    }
}
