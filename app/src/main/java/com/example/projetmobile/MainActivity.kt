package com.example.projetmobile

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projetmobile.components.BottomBar
import com.example.projetmobile.components.HomeScreen
import com.example.projetmobile.components.SubjectsScreen
import com.example.projetmobile.components.TopBar
import com.example.projetmobile.ui.theme.ProjetMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetMobileTheme {
                MyApp()
            }
        }
    }

    @Composable
    fun MyApp() {
        MyScreenPortrait()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyScreenPortrait() {

        val navController = rememberNavController()
        val showBars = remember { mutableStateOf(true) }
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(bottomBar = {
            if (showBars.value) {
                BottomBar(navController)
            }
        },

            topBar = {
                if (showBars.value) {
                    TopBar(navController)
                }
            }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingV ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingV),
            ) {
                composable("home") {
                    showBars.value = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        HomeScreen(paddingV)
                    }
                }
                composable("memoryAid") {
                    showBars.value = true
                    com.example.projetmobile.components.MemoryAidScreen()
                }
                composable("loadingSubjects") {
                    showBars.value = true
                    SubjectsScreen()
                }
            }
        }
    }
}