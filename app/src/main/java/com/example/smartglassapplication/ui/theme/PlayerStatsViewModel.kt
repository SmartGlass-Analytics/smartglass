package com.example.smartglassapplication.ui.theme

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import com.example.smartglassapplication.R
import com.example.smartglassapplication.data.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.File
import java.util.Locale

class PlayerStatsViewModel : ViewModel() {

    /** Pre‑loaded Alabama roster with blank stats */
    val players = mutableStateListOf(
        Player("Mark Sears",       imageRes = R.drawable.marksears),
        Player("Labaron Philon",   imageRes = R.drawable.labaronphilon),
        Player("Chris Youngblood", imageRes = R.drawable.chrisyoungblood),
        Player("Grant Nelson",     imageRes = R.drawable.grantnelson),
        Player("Jarin Stevenson",  imageRes = R.drawable.jarinstevenson),
        Player("Aden Holloway",    imageRes = R.drawable.adenholloway),
        Player("Aiden Sherrell",   imageRes = R.drawable.aidensherrell),
        Player("Derrion Reid",     imageRes = R.drawable.derrionreid),
        Player("Mouhamed Dioubate",imageRes = R.drawable.mouhameddiabate),
        Player("Clifford Omoruyi", imageRes = R.drawable.cliffordomoruyi)
    )

    /* ---------- Public API ---------- */

    fun refreshStats(ctx: Context) = viewModelScope.launch(Dispatchers.IO) {
        runPython()
        val alabamaStats = parseResultFile(ctx)

        withContext(Dispatchers.Main) {
            players.replaceAll { p ->
                alabamaStats[p.name]?.let { obj ->
                    val pts = obj["points"]?.jsonPrimitive?.int ?: 0
                    val ast = obj["assists"]?.jsonPrimitive?.int ?: 0
                    val reb = (obj["oreb"]?.jsonPrimitive?.int ?: 0) +
                            (obj["dreb"]?.jsonPrimitive?.int ?: 0)

                    val summary = "$pts Pts, $reb Reb, $ast Ast"  // ← no “+”
                    val sheet   = obj.entries.joinToString("\n") { (k, v) ->
                        "${k.replace('_', ' ').uppercase(Locale.US)}: ${v.jsonPrimitive.content}"
                    }
                    p.copy(summary = summary, stats = sheet)
                } ?: p
            }
        }
    }

    /* ---------- Helpers ---------- */

    private fun parseResultFile(ctx: Context): Map<String, JsonObject> {
        val file = File(ctx.filesDir, "result.json")
        if (!file.exists()) return emptyMap()

        val root: JsonObject = Json.decodeFromString(file.readText())
        return root.filter { (_, elem) ->
            elem.jsonObject["team"]?.jsonPrimitive?.content == "Alabama Crimson Tide"
        }.mapValues { (_, elem) -> elem.jsonObject }
    }

    private fun runPython() {
        val py  = Python.getInstance()
        val mod = py.getModule("apipractice")

        val token = mod.callAttr(
            "get_access_token",
            mod["TOKEN_URL"].toString(),
            mod["CLIENT_ID"].toString(),
            mod["CLIENT_SECRET"].toString()
        )
        mod.callAttr("retreive_game_stat", mod["GAME_ID"].toString(), token)
    }
}
