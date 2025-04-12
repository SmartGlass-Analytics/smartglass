package com.example.smartglassapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.smartglassapplication.ui.theme.SmartGlassApplicationTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartglassapplication.ui.theme.HomeScreen
import com.example.smartglassapplication.ui.theme.PlayerStatsScreen
import com.example.smartglassapplication.ui.theme.PlayerProfileScreen
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartGlassApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen(navController) }
                        composable("stats") { PlayerStatsScreen(navController) }
                        composable("profile/{playerName}/{playerPosition}/{playerStats}/{playerImageRes}") { backStackEntry ->
                            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
                            val playerPosition = backStackEntry.arguments?.getString("playerPosition") ?: ""
                            val playerStats = backStackEntry.arguments?.getString("playerStats") ?: ""
                            val playerImageRes = backStackEntry.arguments?.getString("playerImageRes")?.toIntOrNull() ?: 0
                            PlayerProfileScreen(playerName, playerPosition, playerStats, playerImageRes)
                        }
                    }
                }
            }
        }
        if( !Python.isStarted() ) {
            Python.start( AndroidPlatform( this ) )
        }
    }
}
