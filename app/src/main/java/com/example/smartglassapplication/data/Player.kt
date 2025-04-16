package com.example.smartglassapplication.data

/**
 * @param summary   Short line for card (e.g., "17 Pts, 5 Reb, 4 Ast")
 * @param stats     Full multiline sheet shown on the profile screen
 */
data class Player(
    val name: String,
    val position: String = "",
    var summary: String = "—",
    var stats: String = "—",
    val imageRes: Int
)
