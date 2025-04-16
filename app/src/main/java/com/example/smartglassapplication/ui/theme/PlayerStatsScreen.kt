package com.example.smartglassapplication.ui.theme

import PlayerCard
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chaquo.python.Python
import com.example.smartglassapplication.R
import com.example.smartglassapplication.data.Player
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import java.io.File

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
            // button code should go here?
            item{
                StatsScreen(title = "Get Game Stats")
            }
            items(players.size) { index ->
                PlayerCard(player = players[index], navController = navController)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun StatsScreen(title: String){
    val context = LocalContext.current
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF9E1B32))
            .clickable { getBoxScore(context=context) },
        textAlign = TextAlign.Center,
        color = Color.Black,
        )
}

fun getJsonObjectFromFile(filePath: String): JsonObject? {
    return try {
        val jsonString = File(filePath).readText()
        Json.decodeFromString(JsonObject.serializer(), jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getBoxScore(context: Context){
    val py = Python.getInstance()
    val resultLocation = "/data/user/0/com.example.smartglassapplication/files/result.json"
    val lineupLocation = "/data/user/0/com.example.smartglassapplication/files/lineup.json"
    val module = py.getModule( "apipractice" )

    val token_url = module["TOKEN_URL"].toString()
    val client_id = module["CLIENT_ID"].toString()
    val client_secret = module["CLIENT_SECRET"].toString()
    val token = module.callAttr("get_access_token", token_url, client_id, client_secret)

    val game_id = module["GAME_ID"].toString()
    @Suppress("unused")
    val unused  = module.callAttr("retreive_game_stat", game_id, token)

    val resultJson = getJsonObjectFromFile(resultLocation)
    resultJson?.let {
        val value = resultJson["Mark Sears"] as? JsonObject
        Toast.makeText(context, value?.get("points").toString(), Toast.LENGTH_SHORT).show()
    } ?: println("Failed to read or parse JSON file result.json")

    val unused2 = module.callAttr("lineupStats","Mark Sears", "Aden Holloway",  "Mouhamed Dioubate", "Grant Nelson", "Aiden Sherrell")
    val lineupJson = getJsonObjectFromFile(lineupLocation)
    lineupJson?.let {
        val value = lineupJson["player1"]
        Toast.makeText(context, value.toString(), Toast.LENGTH_SHORT).show()
    } ?: println("Failed to read or parse JSON file lineup.json")
}
