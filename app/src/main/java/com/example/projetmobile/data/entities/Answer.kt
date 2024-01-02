package com.example.projetmobile.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Answer")
data class Answer(
    @PrimaryKey(autoGenerate = true) val idAnswer: Int = 0,
    val answerText: String,
    val isCorrect: Boolean = false
)
