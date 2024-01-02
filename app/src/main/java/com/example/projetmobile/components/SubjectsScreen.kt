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
import com.example.projetmobile.ui.ViewModelSettings

@Composable
fun SubjectsScreen(
    viewModelSettings: ViewModelSettings = viewModel()
) {
    val backgroundColor by viewModelSettings.getBackgroundColor()
        .collectAsState(initial = viewModelSettings.defaultBackgroundColor)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(backgroundColor)),
    ) {
        /*
        TODO: Faire un Composable pour la view Ajouter un Sujet + faire un Composable pour afficher la liste des Sujets
       */
    }
}