package com.example.projetmobile.components

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetmobile.data.entities.Answer
import com.example.projetmobile.ui.MyViewModel
import com.example.projetmobile.ui.SettingsViewModel
import com.example.projetmobile.ui.theme.ButtonColor
import com.example.projetmobile.ui.theme.IconButtonColor
import com.example.projetmobile.ui.theme.PurpleGrey40
import com.example.projetmobile.ui.theme.SelectedColor
import com.example.projetmobile.ui.theme.TextColor

@Composable
fun LoadingQuestionsScreen(
    navController: NavHostController, idSubject: Int, viewModel: MyViewModel = viewModel()
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = (context as? ComponentActivity)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    LaunchedEffect(key1 = idSubject) {
        viewModel.loadQuestionsForRevisionRandomly(idSubject)
    }

    QuestionsScreen(navController = navController, viewModel = viewModel, idSubject = idSubject)
}

@Composable
fun QuestionsScreen(
    navController: NavHostController,
    viewModel: MyViewModel,
    settingsViewModel: SettingsViewModel = viewModel(),
    idSubject: Int
) {
    val backgroundColor by settingsViewModel.getBackgroundColor()
        .collectAsState(initial = settingsViewModel.defaultBackgroundColor)

    var selectedAnswer by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf<Answer?>(null) }
    var isNoAnswerSelected by remember { mutableStateOf(false) }
    var goodAnswer by remember { mutableStateOf(false) }
    var wrongAnswer by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    val questionsWithAnswers by viewModel.questionsWithAnswers.collectAsState(emptyList())

    if (questionsWithAnswers.isNotEmpty()) {
        val currentQuestion = questionsWithAnswers.first()
        val correctAnswerState by viewModel.getCorrectAnswer(
            idSubject, currentQuestion.question.idQuestion
        ).collectAsState(initial = null)
        correctAnswer = correctAnswerState

        val answers = currentQuestion.answers

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(backgroundColor)),
        ) {

            TopQuestionSection(navController = navController)

            QuestionAnswersSection(
                currentQuestion = currentQuestion,
                answers = answers,
                onAnswerSelected = { answer ->
                    selectedAnswer = answer
                },
                onValidateClicked = {
                    if (selectedAnswer.isNotEmpty() && (correctAnswer != null)) {
                        if ((correctAnswer?.answerText!!.lowercase().replace(
                                Regex("[^A-Za-z0-9 ]"),
                                ""
                            ) == selectedAnswer.lowercase().replace(
                                Regex("[^A-Za-z0-9 ]"),
                                ""
                            )) && (correctAnswer?.isCorrect == true)
                        ) {
                            goodAnswer = true
                            val updatedQuestion = currentQuestion.question.copy(
                                status = currentQuestion.question.status + 1,
                                ans = true,
                                nextRevisionDate = viewModel.calculateNextRevisionDate(
                                    currentQuestion.question.status + 1
                                )
                            )
                            viewModel.updateQuestion(updatedQuestion)
                            viewModel.deleteQuestion(currentQuestion.question.idQuestion)
                            if (viewModel.getNumberOfQuestions() == 0) {
                                viewModel.reloadQuestionsForSubject(idSubject)
                                navController.navigate("MemoryAidScreen")
                            }
                        } else {
                            wrongAnswer = true
                            val updatedStatus = currentQuestion.question.status - 1
                            if (updatedStatus > 0) {
                                val updatedQuestion = currentQuestion.question.copy(
                                    status = updatedStatus,
                                    ans = false,
                                    nextRevisionDate = viewModel.calculateNextRevisionDate(
                                        currentQuestion.question.status - 1
                                    )
                                )
                                viewModel.updateQuestion(updatedQuestion)
                            }
                            viewModel.deleteQuestion(currentQuestion.question.idQuestion)
                            if (viewModel.getNumberOfQuestions() == 0) {
                                viewModel.reloadQuestionsForSubject(idSubject)
                                navController.navigate("MemoryAidScreen")
                            }
                        }
                        selectedAnswer = ""
                    } else {
                        isNoAnswerSelected = true
                    }
                },
                navController = navController,
                viewModel = viewModel,
            )


            if (isNoAnswerSelected) {
                AlertDialog(onDismissRequest = { isNoAnswerSelected = false },
                    title = { Text("Problème de Réponse") },
                    text = { Text("Vous n'avez pas répondu à la question !") },
                    confirmButton = {
                        Button(
                            onClick = { isNoAnswerSelected = false },
                            colors = ButtonDefaults.buttonColors(
                                ButtonColor, Color.White
                            ),
                        ) {
                            Text("OK")
                        }
                    })
            }

            if (goodAnswer) {
                LaunchedEffect(Unit) {
                    showDialog = true
                }
                AlertDialog(onDismissRequest = { goodAnswer = false },
                    title = { Text("Bonne réponse !") },
                    text = { Text("Bravo ! Vous avez trouvé la bonne réponse !") },
                    confirmButton = {
                        Button(
                            onClick = {
                                goodAnswer = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                ButtonColor, Color.White
                            ),
                        ) {
                            Text("OK")
                        }
                    })
            }

            if (wrongAnswer) {
                LaunchedEffect(Unit) {
                    showDialog = true
                }
                AlertDialog(onDismissRequest = { wrongAnswer = false },
                    title = { Text("Mauvaise réponse !") },
                    text = { Text("Mince ! Vous n'avez pas trouvé la bonne réponse !") },
                    confirmButton = {
                        Button(
                            onClick = {
                                wrongAnswer = false
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
}


@Composable
fun AnswerItem(
    text: String,
    onAnswerSelected: (String) -> Unit,
    isSelected: Boolean,
) {
    val backgroundColor = if (isSelected) {
        SelectedColor
    } else {
        Color.Transparent
    }

    Text(
        text = text,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onAnswerSelected(text)
            }
            .padding(8.dp)
            .background(color = backgroundColor),
        style = TextStyle(
            color = Color.Black, fontSize = 16.sp
        ),
    )
}

@Composable
fun TopQuestionSection(
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PurpleGrey40),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = IconButtonColor
            )
        }
        Text(
            text = "Retour", fontSize = 20.sp, color = TextColor, style = TextStyle(
                fontStyle = FontStyle.Italic
            )
        )

    }
}

@Composable
fun QuestionAnswersSection(
    navController: NavHostController,
    currentQuestion: MyViewModel.QuestionAnswerPair,
    answers: List<Answer>,
    onAnswerSelected: (String) -> Unit,
    onValidateClicked: () -> Unit,
    viewModel: MyViewModel,
) {

    var selectedAnswer by remember { mutableStateOf("") }
    var deleteQuestion by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = currentQuestion.question.questionText,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = TextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 25.dp, end = 20.dp),
            )
        }

        if (answers.size > 1) {
            items(answers) { answer ->
                AnswerItem(
                    text = answer.answerText,  onAnswerSelected = {
                        selectedAnswer = it
                        onAnswerSelected(it)
                    }, isSelected = selectedAnswer == answer.answerText
                )
            }
        } else {
            item {
                OutlinedTextField(
                    value = selectedAnswer,
                    onValueChange = {
                        selectedAnswer = it
                        onAnswerSelected(it)
                    },
                    label = { Text("Votre réponse", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp),
                )
            }
        }

        item {
            Button(
                onClick = {
                    onValidateClicked()
                    selectedAnswer = ""
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 25.dp),
                colors = ButtonDefaults.buttonColors(
                    ButtonColor, Color.White
                ),
            ) {
                Text(
                    text = "Valider ma réponse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        item {
            Button(
                onClick = {
                    deleteQuestion = true
                    selectedAnswer = ""
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 25.dp),
                colors = ButtonDefaults.buttonColors(
                    ButtonColor, Color.White
                ),
            ) {
                Text(
                    text = "Supprimer la question",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        item {
            Button(
                onClick = {
                    showAnswer = true
                    selectedAnswer = ""
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 25.dp),
                colors = ButtonDefaults.buttonColors(
                    ButtonColor, Color.White
                ),
            ) {
                Text(
                    text = "Afficher la réponse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        if (deleteQuestion) {
            item {
                AlertDialog(onDismissRequest = { deleteQuestion = false },
                    title = { Text(text = "Confirmation") },
                    text = { Text(text = "Êtes-vous sûr de vouloir supprimer cette question ?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteQuestionAndHisAnswers(currentQuestion.question.idQuestion)
                                deleteQuestion = false
                                viewModel.deleteQuestion(currentQuestion.question.idQuestion)
                                if (viewModel.getNumberOfQuestions() == 0) {
                                    navController.navigate("MemoryAidScreen")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                ButtonColor, Color.White
                            ),
                        ) {
                            Text(text = "Confirmer")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                deleteQuestion = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                ButtonColor, Color.White
                            ),
                        ) {
                            Text(text = "Annuler")
                        }
                    })
            }
        }

        if (showAnswer) {
            item {
                AlertDialog(onDismissRequest = { showAnswer = false },
                    title = { Text(text = "Réponse") },
                    text = {
                        val correctAnswer = currentQuestion.answers.find { it.isCorrect }

                        if (correctAnswer != null) {
                            Text(text = "La bonne réponse est : ${correctAnswer.answerText}")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showAnswer = false
                                viewModel.deleteQuestion(currentQuestion.question.idQuestion)
                                if (viewModel.getNumberOfQuestions() == 0) {
                                    navController.navigate("MemoryAidScreen")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                ButtonColor, Color.White
                            ),
                        ) {
                            Text(text = "OK")
                        }
                    })
            }
        }
    }
}





