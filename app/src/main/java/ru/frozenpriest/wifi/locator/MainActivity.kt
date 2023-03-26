package ru.frozenpriest.wifi.locator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.frozenpriest.wifi.locator.service.WifiScannerService
import ru.frozenpriest.wifi.locator.theme.TemplateTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TemplateTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }

        val permissions = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        _requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    private val _requestPermissionLauncher = registerForActivityResult(RequestMultiplePermissions()) { permissions ->

        val allPermissionsGranted = permissions.values.fold(true) { permissionsGranted, currentPermissionGranted ->
            permissionsGranted && currentPermissionGranted
        }

        if (allPermissionsGranted) {
            Timber.d("Rtt is available: ${packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)}")

            Timber.d("Location permission granted, starting scanner service...")
            val serviceIntent = Intent(this, WifiScannerService::class.java)
            startForegroundService(serviceIntent)
        } else {
            Timber.d("Some permissions were denied.")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TemplateTheme {
        Greeting("Android")
    }
}
