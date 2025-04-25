package com.example.smartglassapplication.ui.theme

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.smartglassapplication.bluetooth.BleCommunicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    name: String,
    position: String,
    stats: String,
    imageRes: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    // Parse stats into list of (label, value), strip leading '+' if present
    val statsList: List<Pair<String, String>> = remember(stats) {
        stats.lineSequence()
            .mapNotNull { it.split(":", limit = 2).takeIf { it.size == 2 } }
            .map { (rawLabel, rawValue) ->
                val label = rawLabel.trim().removePrefix("+")
                val value = rawValue.trim().removePrefix("+")
                label to value
            }
            .toList()
    }

    // Map to track selection; start all unchecked
    val selectedMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            statsList.forEach { (label, _) -> this[label] = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = name,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ) }
            )
        },
        bottomBar = {
            // Send button always visible
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val chosen = statsList
                            .filter { (label, _) -> selectedMap[label] == true }
                            .joinToString("; ") { (l, v) -> "$l: $v" }
                        if (chosen.isBlank()) {
                            Toast.makeText(
                                context,
                                "Select at least one stat",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val ble = BleCommunicator(context)
                            Log.d("BLE", "Chosen Stats: $chosen")
                            ble.connectToDevice(chosen)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Send to Glasses",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player header without colored background
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageRes),
                    contentDescription = name,
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.LightGray, RoundedCornerShape(80.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                if (position.isNotBlank()) {
                    Text(
                        text = position,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Select stats to send",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    statsList.forEachIndexed { idx, (label, value) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = selectedMap[label] == true,
                                onCheckedChange = { checked ->
                                    selectedMap[label] = checked
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "$label: $value",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (idx < statsList.lastIndex) HorizontalDivider()
                    }
                }
            }

            Spacer(Modifier.height(80.dp)) // leave room for bottomBar
        }
    }
}
