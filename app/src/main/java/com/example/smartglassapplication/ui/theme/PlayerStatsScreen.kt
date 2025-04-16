package com.example.smartglassapplication.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatsScreen(
    navController: NavController,
    viewModel: PlayerStatsViewModel = viewModel()
) {
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Stats", color = Color(0xFF9E1B32)) }
            )
        }
    ) { pad ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {
            item {
                StatsButton("Get Game Stats") { viewModel.refreshStats(ctx) }
                Spacer(Modifier.height(12.dp))
            }
            items(viewModel.players) { p ->
                PlayerCard(p, navController)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

/* ---------- reusable button ---------- */

@Composable
fun StatsButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF9E1B32))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center,
        color = Color.White
    )
}
