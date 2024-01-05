package com.example.projetmobile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetmobile.R
import com.example.projetmobile.ui.DownloadViewModel
import com.example.projetmobile.ui.MyViewModel
import com.example.projetmobile.ui.SettingsViewModel
import com.example.projetmobile.ui.theme.ButtonColor

@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    paddingV: PaddingValues,
    viewModelSettings: SettingsViewModel = viewModel(),
    downloadViewModel: DownloadViewModel = viewModel(),
    myViewModel: MyViewModel = viewModel()
) {
    val backgroundColor by viewModelSettings.getBackgroundColor()
        .collectAsState(initial = viewModelSettings.defaultBackgroundColor)

    val bodyFS by viewModelSettings.bodyFontSizeFlow.collectAsState(initial = 14)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(backgroundColor)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = "home",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(paddingV),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp, end = 60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth()
                    .padding(start = 8.dp),
            ) {
                Button(
                    modifier = Modifier.padding(bottom = 15.dp),
                    onClick = {
                        downloadViewModel.doDownload(myViewModel, snackbarHostState)
                    },
                    colors = ButtonDefaults.buttonColors(
                        ButtonColor, Color.White
                    ),
                ) {
                    Text(
                        text = "Charger des jeux de questions", fontSize = bodyFS.sp
                    )
                }
            }
        }
    }
}