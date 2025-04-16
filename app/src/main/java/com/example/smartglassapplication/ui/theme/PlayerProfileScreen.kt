package com.example.smartglassapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    name: String,
    position: String,
    stats: String,
    imageRes: Int
) {
    val scroll = rememberScrollState()

    Scaffold(topBar = { TopAppBar(title = { Text(name) }) }) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* --------- Photo, Name, Position (no border) --------- */
            Image(
                painter = rememberAsyncImagePainter(imageRes),
                contentDescription = name,
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))

            Text(
                name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (position.isNotBlank())
                Text(
                    position,
                    style = MaterialTheme.typography.bodyMedium
                )

            Spacer(Modifier.height(24.dp))

            /* --------- Stat sheet --------- */
            Text(
                "Game Stats",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    val lines = stats.split('\n').filter { it.contains(":") }

                    if (lines.isEmpty()) {
                        Text(
                            "No stats available yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        lines.forEachIndexed { idx, line ->
                            val (label, value) = line.split(":", limit = 2)
                            StatRow(label.trim(), value.trim())
                            if (idx != lines.lastIndex)
                                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    val cleanValue = value.removePrefix("+").trim()   // ← removes leading “+”

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text(cleanValue, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

