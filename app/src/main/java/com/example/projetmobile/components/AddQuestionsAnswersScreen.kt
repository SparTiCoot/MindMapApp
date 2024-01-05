package com.example.projetmobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetmobile.ui.MyViewModel
import com.example.projetmobile.ui.SettingsViewModel
import com.example.projetmobile.ui.theme.ButtonColor
import com.example.projetmobile.ui.theme.IconButtonColor
import com.example.projetmobile.ui.theme.OutlinedTextFieldColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionAnswersScreen(
    idSubject: Int,
    navController: NavHostController,
    model: MyViewModel = viewModel(),
    viewModel: SettingsViewModel = viewModel()
) {
    val backgroundColor by viewModel.backgroundColorFlow.collectAsState(initial = 0xFFCCCCCC.toInt())

    var questionText by remember { mutableStateOf("") }
    val answers = remember { mutableStateListOf("") }
    var subjectName by remember { mutableStateOf("") }

    var errorMSG by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        subjectName = model.getNameSubject(idSubject).toString()
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(Color(backgroundColor)),
    ) {
        item {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.size(60.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back",
                    tint = IconButtonColor,
                )
            }

            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = {
                    Text(
                        text = "Nouvelle question",
                        color = OutlinedTextFieldColor,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
            Row {
                Text(
                    text = "La première réponse sera considérée comme la bonne réponse !",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 5.dp),
                )
            }
        }

        itemsIndexed(answers) { index, answer ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = answer,
                    onValueChange = { newValue ->
                        answers[index] = newValue
                    },
                    label = {
                        Text(
                            "Réponse ${index + 1}",
                            color = OutlinedTextFieldColor,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                )
                IconButton(
                    onClick = {
                        if (answers.size > 1) {
                            answers.removeAt(index)
                        }
                    },
                    modifier = Modifier.padding(4.dp),
                    enabled = answers.size > 1,
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Supprimer réponse",
                        tint = IconButtonColor,
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        if (questionText.isNotBlank() && answers.any { it.isNotBlank() }) {
                            val nonEmptyAnswers = answers.filter { it.isNotBlank() }
                            model.addQuestionAndAnswers(
                                questionText,
                                idSubject,
                                nonEmptyAnswers,
                            )
                            answers.clear()
                            answers.add("")
                            questionText = ""
                            navController.popBackStack()
                        } else {
                            errorMSG = true
                        }
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        ButtonColor, Color.White
                    ),
                ) {
                    Text(
                        text = "Valider", fontSize = 16.sp
                    )
                }
                if (answers.size < 3) {
                    Button(
                        onClick = {
                            answers.add("")
                        },
                        modifier = Modifier.padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            ButtonColor, Color.White
                        ),
                    ) {
                        Text(
                            text = "Ajouter une réponse", fontSize = 16.sp
                        )
                    }
                }

            }
        }
    }

    if (errorMSG) {
        AlertDialog(onDismissRequest = { errorMSG = false },
            title = { Text("Erreur") },
            text = { Text("Veuillez remplir tout les champs.") },
            confirmButton = {
                Button(onClick = { errorMSG = false }) {
                    Text("OK")
                }
            })
    }
}