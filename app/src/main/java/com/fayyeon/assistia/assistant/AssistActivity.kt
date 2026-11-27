package com.fayyeon.assistia.assistant

import android.app.Activity
import android.os.Bundle

/**
 * Invoked when the user triggers the Assistant gesture (holding home button, corner swipe,
 * or hardware assistant trigger) when Assistia is set as the default assistant.
 *
 * This Activity is styled as completely translucent with no window animation. It executes
 * the user-configured remapped action immediately and finishes without showing any UI window.
 */
class AssistActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Execute mapped action and dismiss instantaneously
        ActionExecutor.execute(applicationContext) {
            finish()
        }
    }
}
