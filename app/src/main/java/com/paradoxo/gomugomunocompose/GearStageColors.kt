package com.paradoxo.gomugomunocompose

import androidx.compose.ui.graphics.Color

fun gearStageColor(index: Int) = when (index) {
    0 -> Color(0xFF250054) // Normal
    1 -> Color(0xFF900606) // Gear 2
    2 -> Color(0xFFD49518)// Gear 3
    3 -> Color(0xFF300101) // Gear 4
    4 -> Color(0xFFA2A2A3) // Gear 5
    else -> Color(0xFF250054) // Normal
}