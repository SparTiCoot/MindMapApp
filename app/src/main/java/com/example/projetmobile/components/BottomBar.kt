package com.example.projetmobile.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.projetmobile.BottomNavItem
import com.example.projetmobile.ui.theme.ButtonColor
import com.example.projetmobile.ui.theme.IconMenuTopBottomColor

@Composable
fun BottomBar(navController: NavHostController) {
    val navItems = listOf(
        BottomNavItem("home", Icons.Default.Home, "Accueil"),
        BottomNavItem("memoryAid", Icons.Default.Person, "Aide Mémoire"),
        BottomNavItem("loadingSubjects", Icons.Default.List, "Les Sujets"),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(backgroundColor = ButtonColor) {
        navItems.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        if (item.route != "home") {
                            popUpTo("home")
                        }
                    }
                },
                icon = { Icon(item.icon, item.contentDescription, tint = IconMenuTopBottomColor) },
                label = {
                    if (currentRoute == item.route) {
                        Text(
                            text = item.label, fontSize = 13.sp, color = IconMenuTopBottomColor
                        )
                    } else {
                        Text(text = "", color = IconMenuTopBottomColor)
                    }
                },
            )
        }
    }
}