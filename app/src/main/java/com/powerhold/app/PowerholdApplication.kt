package com.powerhold.app

import android.app.Application
import com.powerhold.app.data.PreferencesManager

class PowerholdApplication : Application() {

    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesManager = PreferencesManager(this)
    }

    companion object {
        lateinit var instance: PowerholdApplication
            private set
    }
}
