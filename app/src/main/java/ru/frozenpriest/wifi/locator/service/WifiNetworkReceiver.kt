package ru.frozenpriest.wifi.locator.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.frozenpriest.wifi.locator.core.ActionBlock

class WifiNetworkReceiver(private val wifiScanFinished: ActionBlock) : BroadcastReceiver() {

// MARK: - Methods

    override fun onReceive(context: Context, intent: Intent) {
        wifiScanFinished.invoke()
    }
}
