package com.example.smartglassapplication.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartglassapplication.R
import com.example.smartglassapplication.data.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatsScreen() {
    val players = listOf(
        Player("M. Sears", "Point Guard", "5 Pts, 4 Ast, 1 Reb", R.drawable.player_avatar),
        Player("L. Philon", "Shooting Guard", "10 Pts, 1 Ast, 2 Reb", R.drawable.player_avatar),
        Player("C. Youngblood", "Small Forward", "8 Pts, 0 Ast, 4 Reb", R.drawable.player_avatar),
        Player("G. Nelson", "Power Forward", "2 Pts, 0 Ast, 6 Reb", R.drawable.player_avatar),
        Player("C. Omoruyi", "Center", "3 Pts, 1 Ast, 11 Reb", R.drawable.player_avatar)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Alabama Smartglass Stats") })
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(players.size) { index ->
                PlayerCard(player = players[index])
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
