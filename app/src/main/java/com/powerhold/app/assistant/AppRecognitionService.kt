package com.powerhold.app.assistant

import android.content.Intent
import android.speech.RecognitionService

/**
 * RecognitionService companion required by Android's VoiceInteraction subsystem
 * so Powerhold is recognized and selectable as a system Digital Assistant on all OEM ROMs.
 */
class AppRecognitionService : RecognitionService() {
    override fun onStartListening(recognizerIntent: Intent?, listener: Callback?) {
        // Powerhold acts as an instant tactile trigger remapper, no microphone processing needed
    }

    override fun onCancel(listener: Callback?) {}

    override fun onStopListening(listener: Callback?) {}
}
