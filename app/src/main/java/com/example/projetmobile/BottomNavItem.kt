package com.example.projetmobile

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String = "",
    val contentDescription: String = label,
)