package com.example.projetmobile.ui

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetmobile.data.LearnASubjectApplication
import com.example.projetmobile.data.entities.Answer
import com.example.projetmobile.data.entities.Question
import com.example.projetmobile.data.entities.Subject
import com.example.projetmobile.data.entities.SubjectQuestionAnswer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val myDao = (application as LearnASubjectApplication).database.myDao()

    data class QuestionAnswerPair(
        val question: Question, val answers: List<Answer>
    )

    private val _questionsWithAnswers = MutableStateFlow<List<QuestionAnswerPair>>(emptyList())
    val questionsWithAnswers: StateFlow<List<QuestionAnswerPair>> get() = _questionsWithAnswers.asStateFlow()

    private var errorIns = mutableStateOf(false)
    private val comptIns = mutableIntStateOf(0)

    fun addSubjectIfNotExists(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingSubject = myDao.checkIfSubjectExists(name)
            if (existingSubject == null) {
                val res = async { myDao.insertSubject(Subject(name = name)) }
                errorIns.value = (res.await() == -1L)
                comptIns.intValue++
            }
        }
    }

    fun addQuestionAndAnswers(
        questionText: String,
        idSubject: Int,
        answers: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingQuestion = myDao.checkIfQuestionExists(questionText, idSubject)

            if (existingQuestion == null) {
                val newQuestion = Question(
                    questionText = questionText,
                    idSubject = idSubject,
                )
                val questionId = myDao.insertQuestionAndGetId(newQuestion)

                answers.forEachIndexed { index, answerText ->
                    val existingAnswer = myDao.checkIfAnswerExists(answerText, idSubject)
                    val isCorrect = index == 0

                    if (existingAnswer == null) {
                        val newAnswer = Answer(answerText = answerText, isCorrect = isCorrect)
                        val answerId = myDao.insertAnswerAndGetId(newAnswer)

                        myDao.insertSubjectQuestionAnswer(
                            SubjectQuestionAnswer(
                                idSubject = idSubject,
                                idQuestion = questionId.toInt(),
                                idAnswer = answerId.toInt()
                            )
                        )
                    }
                }
            }
        }
    }

    fun getSubjects(): Flow<List<Subject>> {
        return myDao.getAllSubjects()
    }

    fun getNameSubject(idSubject: Int): Flow<String> {
        return myDao.getSubjectNameByIdSubject(idSubject)
    }

    fun loadQuestionsAnswersForSubjectOrdered(idSubject: Int) {
        viewModelScope.launch {
            val questionAnswerPairs = mutableListOf<QuestionAnswerPair>()

            val questions = myDao.getQuestionsBySubjectIdOrdered(idSubject).first().distinct()

            questions.forEach { question ->
                val answers =
                    myDao.getAnswersBySubjectAndQuestionIdOrdered(idSubject, question.idQuestion)
                        .firstOrNull() ?: emptyList()

                questionAnswerPairs.add(QuestionAnswerPair(question, answers.distinct()))
            }
            _questionsWithAnswers.value = questionAnswerPairs
        }
    }

    fun deleteSubjectAndHisQuestion(idSubject: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            myDao.deleteAnswersBySubjectId(idSubject)
            myDao.deleteQuestionsBySubjectId(idSubject)
            myDao.deleteSubject(idSubject)
        }
    }

    fun deleteQuestionAndHisAnswers(idQuestion: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            myDao.deleteAnswersByQuestionId(idQuestion)
            myDao.deleteQuestion(idQuestion)
            _questionsWithAnswers.value = _questionsWithAnswers.value.filter {
                it.question.idQuestion != idQuestion
            }
        }
    }

    fun deleteAnswer(idAnswer: Int) {
        viewModelScope.launch {
            myDao.deleteAnswer(idAnswer)
            _questionsWithAnswers.value = _questionsWithAnswers.value.map { pair ->
                pair.copy(answers = pair.answers.filter { it.idAnswer != idAnswer })
            }
        }
    }

}