package com.example.smartglassapplication

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.smartglassapplication.ui.theme.HomeScreen
import com.example.smartglassapplication.ui.theme.PlayerProfileScreen
import com.example.smartglassapplication.ui.theme.PlayerStatsScreen
import com.example.smartglassapplication.ui.theme.SmartGlassApplicationTheme

class MainActivity : ComponentActivity() {

    // Launcher for runtime permission requests
    private val permissionLauncher =
        registerForActivityResult(RequestMultiplePermissions()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request BLE & location perms before doing any scanning
        requestAllPermissions()

        setContent {
            SmartGlassApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("stats") {
                            PlayerStatsScreen(navController)
                        }
                        composable(
                            "profile/{playerName}/{playerPosition}/{playerStats}/{playerImageRes}"
                        ) { backStackEntry ->
                            val args  = backStackEntry.arguments
                            val name  = args?.getString("playerName") ?: ""
                            val pos   = args?.getString("playerPosition") ?: ""
                            val stats = args?.getString("playerStats") ?: ""
                            val img   = args?.getString("playerImageRes")?.toIntOrNull() ?: 0
                            PlayerProfileScreen(name, pos, stats, img)
                        }
                    }
                }
            }
        }

        // Initialize Chaquopy (Python)
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }

    /**
     * Always ask for ACCESS_FINE_LOCATION (needed for BLE scanning shim),
     * plus BLUETOOTH_SCAN & BLUETOOTH_CONNECT on Android 12+
     */
    private fun requestAllPermissions() {
        val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            perms += Manifest.permission.BLUETOOTH_SCAN
            perms += Manifest.permission.BLUETOOTH_CONNECT
        }
        val missing = perms.any { perm ->
            ContextCompat.checkSelfPermission(this, perm) !=
                    PermissionChecker.PERMISSION_GRANTED
        }
        if (missing) {
            permissionLauncher.launch(perms.toTypedArray())
        }
    }
}
