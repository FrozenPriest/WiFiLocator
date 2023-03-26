@file:SuppressLint("MissingPermission")

package ru.frozenpriest.wifi.locator.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import timber.log.Timber

class WifiNetworkReceiver(wifiManager: WifiManager) : BroadcastReceiver() {

// MARK: - Methods

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Got wifi: ${_wifiManager.scanResults}")
    }

// MARK: - Variables

    private val _wifiManager: WifiManager = wifiManager
}
