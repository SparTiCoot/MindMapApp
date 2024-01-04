package com.example.projetmobile.ui

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.pow

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val myDao = (application as LearnASubjectApplication).database.myDao()

    data class QuestionAnswerPair(
        val question: Question, val answers: List<Answer>
    )

    private val _questionsWithAnswers = MutableStateFlow<List<QuestionAnswerPair>>(emptyList())
    val questionsWithAnswers: StateFlow<List<QuestionAnswerPair>> get() = _questionsWithAnswers.asStateFlow()

    private val _numberOfQuestions = MutableStateFlow(0)
    private val numberOfQuestions: StateFlow<Int> get() = _numberOfQuestions.asStateFlow()

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

    fun getNumberOfQuestions(): Int {
        return numberOfQuestions.value
    }

    fun getCorrectAnswer(subjectId: Int, questionId: Int): Flow<Answer?> {
        return myDao.getCorrectAnswer(subjectId, questionId)
    }

    suspend fun getTotalQuestion(idSubject: Int): Int {
        return withContext(Dispatchers.IO) {
            myDao.getTotalQuestions(idSubject)
        }
    }

    suspend fun getTotalGAns(idSubject: Int): Int {
        return withContext(Dispatchers.IO) {
            myDao.getTotalGAns(idSubject)
        }
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

    suspend fun loadQuestionsForRevisionRandomly(idSubject: Int) {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                val currentDate = System.currentTimeMillis()
                val questionsToRevise =
                    myDao.getQuestionsForRevisionRandomly(idSubject, currentDate).first().distinct()
                val questionAnswerPairs = mutableListOf<QuestionAnswerPair>()

                questionsToRevise.forEach { question ->
                    val answers =
                        myDao.getAnswersBySubjectAndQuestionIdRandomly(
                            idSubject,
                            question.idQuestion
                        )
                            .firstOrNull() ?: emptyList()

                    questionAnswerPairs.add(QuestionAnswerPair(question, answers.distinct()))
                }
                _questionsWithAnswers.value = questionAnswerPairs
                updateNumberOfQuestions(questionAnswerPairs)
                continuation.resume(Unit)
            }
        }
    }

    fun reloadQuestionsForSubject(idSubject: Int) {
        viewModelScope.launch {
            _questionsWithAnswers.value = emptyList()
            loadQuestionsForRevisionRandomly(idSubject)
        }
    }

    fun updateQuestion(question: Question) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                myDao.updateQuestion(question)
            }
        }
    }



    private fun updateNumberOfQuestions(updatedQuestions: List<QuestionAnswerPair>) {
        _numberOfQuestions.value = updatedQuestions.size
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

    fun deleteQuestion(idQuestion: Int) {
        val updatedQuestions = questionsWithAnswers.value.toMutableList()
        val index = updatedQuestions.indexOfFirst { it.question.idQuestion == idQuestion }

        if (index != -1) {
            updatedQuestions.removeAt(index)
            _questionsWithAnswers.value = updatedQuestions

            _numberOfQuestions.value = updatedQuestions.size
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateNextRevisionDate(status: Int): Long {
        val revisionPeriod = (2.0.pow(status - 1) * 12 * 60 * 60 * 1000).toLong()
        val currentDate = System.currentTimeMillis()
        val nextRevisionDate = currentDate + revisionPeriod

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val formattedDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(nextRevisionDate),
            ZoneId.systemDefault()
        ).format(formatter)

        Log.d("TAG", "Next Revision Date: $formattedDate")
        return nextRevisionDate
    }

}