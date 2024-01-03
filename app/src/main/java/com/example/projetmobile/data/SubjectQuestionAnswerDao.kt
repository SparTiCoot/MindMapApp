package com.example.projetmobile.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.projetmobile.data.entities.Answer
import com.example.projetmobile.data.entities.Question
import com.example.projetmobile.data.entities.Subject
import com.example.projetmobile.data.entities.SubjectQuestionAnswer
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectQuestionAnswerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubject(subject: Subject): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestionAndGetId(question: Question): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnswerAndGetId(answer: Answer): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubjectQuestionAnswer(subjectQuestionAnswer: SubjectQuestionAnswer)

    @Query("SELECT * FROM Subject")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT s.name FROM subject s WHERE idSubject =:idSubject")
    fun getSubjectNameByIdSubject(idSubject: Int): Flow<String>

    @Transaction
    @Query(
        """
        SELECT q.*
        FROM Question q
        LEFT JOIN SubjectQuestionAnswer sqa ON sqa.idQuestion = q.idQuestion
        WHERE q.idSubject = :idSubject
        ORDER BY q.idQuestion
    """,
    )
    fun getQuestionsBySubjectIdOrdered(idSubject: Int): Flow<List<Question>>

    @Transaction
    @Query(
        """
    SELECT a.idAnswer, a.answerText, a.isCorrect
    FROM SubjectQuestionAnswer sqa
    INNER JOIN Answer a ON sqa.idAnswer = a.idAnswer
    WHERE sqa.idSubject = :idSubject AND sqa.idQuestion=:idQuestion ORDER BY sqa.idQuestion
    """,
    )
    fun getAnswersBySubjectAndQuestionIdOrdered(
        idSubject: Int,
        idQuestion: Int,
    ): Flow<List<Answer>>

    @Query("DELETE FROM Subject WHERE idSubject=:idSubject")
    suspend fun deleteSubject(idSubject: Int)

    @Query("DELETE FROM Question WHERE idQuestion=:idQuestion")
    suspend fun deleteQuestion(idQuestion: Int)

    @Query("DELETE FROM Question WHERE idSubject = :idSubject")
    suspend fun deleteQuestionsBySubjectId(idSubject: Int)

    @Query("DELETE FROM answer WHERE idAnswer=:idAnswer")
    suspend fun deleteAnswer(idAnswer: Int)

    @Query("DELETE FROM Answer WHERE idAnswer IN (SELECT idAnswer FROM SubjectQuestionAnswer WHERE idQuestion IN (SELECT idQuestion FROM Question WHERE idSubject = :idSubject))")
    suspend fun deleteAnswersBySubjectId(idSubject: Int)

    @Query("DELETE FROM Answer WHERE idAnswer IN (SELECT idAnswer FROM SubjectQuestionAnswer WHERE idQuestion = :idQuestion)")
    suspend fun deleteAnswersByQuestionId(idQuestion: Int)

    @Query("SELECT * FROM Subject WHERE name = :subjectName LIMIT 1")
    fun checkIfSubjectExists(subjectName: String): Subject?

    @Query("SELECT * FROM Question WHERE questionText = :questionText AND idSubject = :idSubject LIMIT 1")
    fun checkIfQuestionExists(
        questionText: String,
        idSubject: Int,
    ): Question?

    @Query("SELECT a.idAnswer, a.answerText, a.isCorrect FROM Answer a JOIN SubjectQuestionAnswer sqa ON a.idAnswer = sqa.idAnswer WHERE answerText = :answerText AND idSubject = :idSubject LIMIT 1")
    fun checkIfAnswerExists(answerText: String, idSubject: Int): Answer?
}