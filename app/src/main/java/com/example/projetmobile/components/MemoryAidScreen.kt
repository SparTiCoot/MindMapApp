package com.example.projetmobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.projetmobile.ui.theme.ButtonColor
import com.example.projetmobile.ui.theme.ColorUtils
import com.example.projetmobile.ui.theme.TextColor
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun MemoryAidScreen(
    navController: NavHostController,
    model: MyViewModel = viewModel(),
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val subjects by model.getSubjects().collectAsState(initial = emptyList())

        SubjectsPlayView(navController, subjects, model)
    }
}

@Composable
fun SubjectsPlayView(
    navController: NavHostController,
    subjects: List<Subject>,
    model: MyViewModel,
) {

    var showDialogNeedToWait by remember { mutableStateOf(false) }

    var isLoadingQuestions by remember { mutableStateOf(false) }
    var subjectIdToSend by remember { mutableIntStateOf(-1) }

    val totalQuestionsList = remember { mutableStateListOf<Int>() }
    if (totalQuestionsList.isEmpty()) {
        totalQuestionsList.addAll(List(subjects.size) { 0 })
    }

    val totalGAsList = remember { mutableStateListOf<Int>() }
    if (totalGAsList.isEmpty()) {
        totalGAsList.addAll(List(subjects.size) { 0 })
    }

    val showDetailsList = remember { mutableStateListOf<Boolean>() }
    val checkedList = remember { mutableStateListOf<Boolean>() }

    if (showDetailsList.isEmpty()) {
        showDetailsList.addAll(List(subjects.size) { false })
    }

    if (checkedList.isEmpty()) {
        checkedList.addAll(List(subjects.size) { false })
    }

    Column {
        LazyColumn {
            itemsIndexed(subjects) { index, subject ->
                val backgroundColor = ColorUtils.colorsList[index % ColorUtils.colorsList.size]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(backgroundColor, RoundedCornerShape(15.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = checkedList.getOrElse(index) { false },
                        onCheckedChange = { isChecked ->
                            checkedList[index] = isChecked
                            showDetailsList[index] = isChecked
                        },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = CheckboxDefaults.colors(uncheckedColor = Color.DarkGray),
                    )

                    Text(
                        text = subject.name,
                        modifier = Modifier.weight(1f),
                        style = TextStyle(
                            color = TextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )

                    Button(
                        onClick = {
                            subjectIdToSend = subject.idSubject
                            isLoadingQuestions = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            ButtonColor, Color.White
                        ),
                    ) {
                        Text(text = "Jouer")
                    }
                }
                if (showDetailsList.getOrElse(index) { false }) {
                    LaunchedEffect(Unit) {
                        val totalQuestionForSubject = model.getTotalQuestion(subject.idSubject)
                        totalQuestionsList[index] = totalQuestionForSubject
                        val totalGAnswer = model.getTotalGAns(subject.idSubject)
                        totalGAsList[index] = totalGAnswer
                    }
                    Text(
                        text = "Nombre total de questions : ${
                            totalQuestionsList[index]
                        }\n" + "Nombre de bonnes réponses : ${
                            if (totalQuestionsList[index] != 0) "%.2f".format(((totalGAsList[index].toFloat() / totalQuestionsList[index]) * 100))
                            else 0
                        }%",
                        style = TextStyle(
                            color = TextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        LaunchedEffect(subjectIdToSend, isLoadingQuestions) {
            if (isLoadingQuestions && subjectIdToSend != -1) {
                model.loadQuestionsForRevisionRandomly(subjectIdToSend)
                val questionsWithAnswers = model.questionsWithAnswers.firstOrNull()
                if (!questionsWithAnswers.isNullOrEmpty()) {
                    navController.navigate("LoadingQuestionsScreen/$subjectIdToSend")
                    isLoadingQuestions = false
                } else {
                    showDialogNeedToWait = true
                    isLoadingQuestions = false
                }
            }

        }

        if (showDialogNeedToWait) {
            AlertDialog(onDismissRequest = { showDialogNeedToWait = false },
                title = { Text("Informations") },
                text = { Text("Vous devez attendre pour avoir de nouveau des questions !\n" +
                        "Sinon vérifier que vous avez enregistré au moins une question dans votre sujet.") },

                confirmButton = {
                    Button(
                        onClick = {
                            showDialogNeedToWait = false
                        },

                        colors = ButtonDefaults.buttonColors(
                            ButtonColor, Color.White
                        ),
                    ) {
                        Text("OK")
                    }
                })
        }
    }
}