package ru.frozenpriest.wifi.locator

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class WifiLocatorApplication : Application() {

// MARK: - Methods

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}
