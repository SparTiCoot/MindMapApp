package com.example.projetmobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetmobile.ui.ViewModelSettings

@Composable
fun MemoryAidScreen(
    viewModelSettings: ViewModelSettings = viewModel()
) {
    val backgroundColor by viewModelSettings.getBackgroundColor()
        .collectAsState(initial = viewModelSettings.defaultBackgroundColor)

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(backgroundColor)),
    ) {

    }
}