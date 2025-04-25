package com.example.smartglassapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.navigation.NavController
import com.example.smartglassapplication.data.Player

@Composable
fun PlayerCard(player: Player, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val encoded = URLEncoder.encode(player.stats, StandardCharsets.UTF_8.toString())
                navController.navigate(
                    "profile/${player.name}/${player.position}/$encoded/${player.imageRes}"
                )
            }
            .padding(8.dp)
    ) {
        Row(Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(player.imageRes),
                contentDescription = player.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(player.name, style = MaterialTheme.typography.titleMedium)
                if (player.position.isNotBlank())
                    Text(player.position, style = MaterialTheme.typography.bodyMedium)
                if (player.summary != "—")
                    Text(player.summary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
