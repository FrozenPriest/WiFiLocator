@file:SuppressLint("MissingPermission")

package ru.frozenpriest.wifi.locator.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.widget.Toast
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
        registerReceiver(_wifiNetworkReceiver, filter)

        launch {
            while (true) {
                val scanStarted = _wifiManager.startScan()
                Timber.d("Scan requested: $scanStarted")

                delay(Time.WIFI_SCAN_PERIOD)
            }
        }
    }

    private fun handleWifiScanFinished() {
        val wifiScanResults = _wifiManager.scanResults
        Timber.d("Wifi scan results:\n${wifiScanResults.joinToString("\n")}")

        val rangingRequest = RangingRequest.Builder()
            .addAccessPoints(wifiScanResults)
            .build()

        _wifiRttManager.startRanging(
            rangingRequest,
            mainExecutor,
            RttResultsListener(),
        )
    }

// MARK: - Inner Types

    private inner class RttResultsListener : RangingResultCallback() {
        override fun onRangingFailure(errorCode: Int) {
            Timber.e("Error using rtt wifi.")
        }

        override fun onRangingResults(results: List<RangingResult>) {
            Timber.d("Wifi rtt results:\n${results.joinToString("\n")}")
            Toast.makeText(this@WifiScannerService, "Got results", Toast.LENGTH_LONG).show()
        }
    }

// MARK: - Constants

    private object Time {
        const val WIFI_SCAN_PERIOD = 5000L
    }

// MARK: - Variables

    private val _wifiNetworkReceiver = WifiNetworkReceiver(this::handleWifiScanFinished)

    private val _applicationNotifications: ApplicationNotifications by lazy {
        ApplicationNotifications(this)
    }

    private val _wifiManager: WifiManager by lazy {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val _wifiRttManager: WifiRttManager by lazy {
        applicationContext.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager
    }

    private inline val _lifecycleScope: CoroutineScope
        get() = this.lifecycleScope
}
