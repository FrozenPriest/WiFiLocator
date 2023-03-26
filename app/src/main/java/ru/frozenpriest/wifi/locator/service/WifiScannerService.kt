@file:SuppressLint("MissingPermission")

package ru.frozenpriest.wifi.locator.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.frozenpriest.wifi.locator.MainActivity
import ru.frozenpriest.wifi.locator.notification.ApplicationNotifications
import timber.log.Timber

class WifiScannerService : LifecycleService() {

// MARK: - Methods

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ 0,
            /* intent = */ notificationIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = _applicationNotifications.createWiFiScannerRunningNotification(pendingIntent)

        startForeground(1, notification)
        requestStartWifiScanner()

        return START_NOT_STICKY
    }

// MARK: - Private Methods

    private fun requestStartWifiScanner() {
        _lifecycleScope.launch {
            startWifiScanner()
        }
    }

    private suspend fun startWifiScanner() = withContext(Dispatchers.IO) {
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiNetworkReceiver, filter)

        launch {
            while (true) {
                val scanStarted = wifiManager.startScan()
                Timber.d("Scan requested: $scanStarted")

                delay(Time.WIFI_SCAN_PERIOD)
            }
        }
    }

    private fun handleWifiScanFinished() {
        val wifiScanResults = wifiManager.scanResults
        Timber.d("Wifi scan results:\n${wifiScanResults.joinToString("\n")}")
    }

// MARK: - Constants

    private object Time {
        const val WIFI_SCAN_PERIOD = 5000L
    }

// MARK: - Variables

    private val wifiNetworkReceiver = WifiNetworkReceiver(this::handleWifiScanFinished)

    private val _applicationNotifications: ApplicationNotifications by lazy {
        ApplicationNotifications(this)
    }

    private val wifiManager: WifiManager by lazy {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private inline val _lifecycleScope: CoroutineScope
        get() = this.lifecycleScope
}
