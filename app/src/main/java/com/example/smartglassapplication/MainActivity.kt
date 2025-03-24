package com.example.smartglassapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.smartglassapplication.ui.theme.PlayerStatsScreen
import com.example.smartglassapplication.ui.theme.SmartglassApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartglassApplicationTheme {
                PlayerStatsScreen()
            }
        }
    }
}
