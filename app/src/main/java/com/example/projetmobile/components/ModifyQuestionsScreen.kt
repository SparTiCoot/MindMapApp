package com.example.projetmobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.projetmobile.ui.ViewModelSettings
import com.example.projetmobile.ui.theme.ButtonColor
import com.example.projetmobile.ui.theme.ColorUtils.colorsList
import com.example.projetmobile.ui.theme.IconButtonColor
import com.example.projetmobile.ui.theme.PurpleGrey40
import com.example.projetmobile.ui.theme.TextColor

@Composable
fun ModifyQuestionsScreen(
    navController: NavHostController,
    idSubject: Int,
    viewModel: MyViewModel = viewModel(),
    viewModelSettings: ViewModelSettings = viewModel()
) {
    val backgroundColor by viewModelSettings.getBackgroundColor()
        .collectAsState(initial = viewModelSettings.defaultBackgroundColor)

    val questionsWithAnswers by viewModel.questionsWithAnswers.collectAsState(emptyList())

    LaunchedEffect(key1 = idSubject) {
        viewModel.loadQuestionsAnswersForSubjectOrdered(idSubject)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(backgroundColor)),
    ) {
        val questionText by remember { mutableStateOf("") }
        DisplayTopBarForModifyQuestionsAndAnswersScreen(
            navController = navController, subjectName = questionText, idSubject = idSubject
        )

        ShowListOfQuestionsAndAnswersBySubjectView(
            questionsWithAnswers, viewModel
        )
    }
}

@Composable
private fun DisplayTopBarForModifyQuestionsAndAnswersScreen(
    navController: NavHostController, subjectName: String, idSubject: Int
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
            modifier = Modifier.size(60.dp),

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
        Text(
            text = subjectName,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
        )
        FloatingActionButton(
            onClick = { navController.navigate("AddQuestionAnswersScreen/$idSubject") },
            modifier = Modifier
                .padding(16.dp)
                .size(35.dp),
            containerColor = ButtonColor,
            contentColor = Color.White
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Ajouter",
            )
        }
    }
}

@Composable
fun ShowListOfQuestionsAndAnswersBySubjectView(
    questionsWithAnswers: List<MyViewModel.QuestionAnswerPair>,
    viewModel: MyViewModel,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(horizontal = 10.dp, vertical = 20.dp)
    ) {
        QuestionList(questionsWithAnswers, viewModel)
    }
}

@Composable
private fun QuestionList(
    questionsAnswers: List<MyViewModel.QuestionAnswerPair>, model: MyViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(questionsAnswers.distinct()) { index, pair ->

            QuestionItem(pair, model, index)
        }
    }
}


@Composable
private fun QuestionItem(
    questionsAnswers: MyViewModel.QuestionAnswerPair, model: MyViewModel, index: Int
) {
    val backgroundColor = colorsList[index % colorsList.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question: ${questionsAnswers.question.questionText}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    fontSize = 12.sp,
                ),
                modifier = Modifier
                    .weight(0.8f)
                    .padding(start = 20.dp)
                    .wrapContentWidth(align = Alignment.Start)
            )
            IconButton(
                onClick = {
                    model.deleteQuestionAndHisAnswers(questionsAnswers.question.idQuestion)
                },
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = IconButtonColor,
                    modifier = Modifier
                        .weight(0.2f)
                        .size(25.dp)
                )
            }
        }
    }
    AnswerList(questionsAnswers.answers.distinct(), model, index)
}

@Composable
private fun AnswerList(
    answers: List<Answer>, model: MyViewModel, index: Int
) {
    val backgroundColor = colorsList[index % colorsList.size]

    answers.forEach { answer ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Réponse: ${answer.answerText}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        fontSize = 10.sp,
                    ),
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(start = 20.dp)
                        .wrapContentWidth(align = Alignment.Start),
                )
                IconButton(
                    onClick = { model.deleteAnswer(answer.idAnswer) },
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = IconButtonColor,
                        modifier = Modifier
                            .weight(0.2f)
                            .size(25.dp)
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
