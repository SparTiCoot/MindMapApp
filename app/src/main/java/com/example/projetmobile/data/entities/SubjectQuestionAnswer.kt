package com.example.projetmobile.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import javax.security.auth.Subject

@Entity(
    indices = [
        Index(value = ["idSubject"]),
        Index(value = ["idQuestion"]),
        Index(value = ["idAnswer"]),
    ],
    primaryKeys = ["idSubject", "idQuestion", "idAnswer"],
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["idSubject"],
            childColumns = ["idSubject"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Question::class,
            parentColumns = ["idQuestion"],
            childColumns = ["idQuestion"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Answer::class,
            parentColumns = ["idAnswer"],
            childColumns = ["idAnswer"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SubjectQuestionAnswer(
    val idSubject: Int,
    val idQuestion: Int,
    val idAnswer: Int,
)
