package com.example.projetmobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetmobile.data.entities.Subject
import com.example.projetmobile.ui.MyViewModel
import com.example.projetmobile.ui.SettingsViewModel
import com.example.projetmobile.ui.theme.ButtonColor
import com.example.projetmobile.ui.theme.PurpleGrey40
import com.example.projetmobile.ui.theme.TextColor

@Composable
fun SubjectsScreen(
    navController: NavHostController,
    model: MyViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val backgroundColor by settingsViewModel.getBackgroundColor()
        .collectAsState(initial = settingsViewModel.defaultBackgroundColor)
    val bodyFS by settingsViewModel.bodyFontSizeFlow.collectAsState(initial = 14)

    val subjects by model.getSubjects().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(backgroundColor)),
    ) {
        AddingSubjectView(onAddSubject = model::addSubjectIfNotExists, bodyFS)
        ShowListOfSubjectsView(
            bodyFS = bodyFS,
            subjects = subjects,
            onDeleteSubject = model::deleteSubjectAndHisQuestion,
            navController = navController,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingSubjectView(onAddSubject: (String) -> Unit, bodyFS: Int) {
    var newSubjectText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .background(PurpleGrey40)
            .fillMaxHeight(0.5f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            OutlinedTextField(
                shape = RoundedCornerShape(50.dp),
                value = newSubjectText,
                onValueChange = { newSubjectText = it },
                label = {
                    Text(
                        text = "Nom du sujet", fontSize = 16.sp, color = Color.White
                    )
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 6.dp)
                    .height(60.dp)
                    .fillMaxWidth(),
            )
            Button(
                onClick = {
                    if (newSubjectText.isNotBlank()) {
                        onAddSubject(newSubjectText)
                        newSubjectText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    ButtonColor, Color.White
                ),
                shape = CutCornerShape(10),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(end = 5.dp),
            ) {
                Text(
                    text = "Ajouter", fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ShowListOfSubjectsView(
    bodyFS: Int,
    subjects: List<Subject>,
    onDeleteSubject: (Int) -> Unit,
    navController: NavHostController,
) {
    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            items(subjects) { subject ->
                SubjectItem(
                    bodyFS = bodyFS,
                    subject = subject,
                    onDeleteSubject = onDeleteSubject,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
fun SubjectItem(
    bodyFS: Int,
    subject: Subject,
    onDeleteSubject: (Int) -> Unit,
    navController: NavHostController,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = {
                navController.navigate("ModifyQuestionsScreen/${subject.idSubject}")
            },
            modifier = Modifier
                .padding(end = 10.dp)
                .width(110.dp),
            colors = ButtonDefaults.buttonColors(
                ButtonColor, Color.White
            ),
        ) {
            Text(
                text = "Modifier", fontSize = bodyFS.sp
            )
        }
        Text(
            text = subject.name,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                color = TextColor,
                fontSize = bodyFS.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        IconButton(onClick = { onDeleteSubject(subject.idSubject) }) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Delete",
                tint = Color.DarkGray,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}