package com.example.smartglassapplication.ui.theme

import PlayerCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartglassapplication.R
import com.example.smartglassapplication.data.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatsScreen(navController: NavController) {
    val players = listOf(
        Player("M. Sears", "Point Guard", "5 Pts, 4 Ast, 1 Reb", R.drawable.marksears),
        Player("L. Philon", "Shooting Guard", "10 Pts, 1 Ast, 2 Reb", R.drawable.labaronphilon),
        Player("C. Youngblood", "Small Forward", "8 Pts, 0 Ast, 4 Reb", R.drawable.chrisyoungblood),
        Player("G. Nelson", "Power Forward", "2 Pts, 0 Ast, 6 Reb", R.drawable.grantnelson),
        Player("C. Omoruyi", "Center", "3 Pts, 1 Ast, 11 Reb", R.drawable.cliffordomoruyi)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Player Stats") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(players.size) { index ->
                PlayerCard(player = players[index], navController = navController)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
