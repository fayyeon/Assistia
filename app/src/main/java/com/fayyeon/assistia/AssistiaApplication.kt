package com.fayyeon.assistia

import android.app.Application
import com.fayyeon.assistia.data.PreferencesManager

class AssistiaApplication : Application() {

    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesManager = PreferencesManager(this)
    }

    companion object {
        lateinit var instance: AssistiaApplication
            private set
    }
}
