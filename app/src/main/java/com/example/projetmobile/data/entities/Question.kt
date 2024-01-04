package com.example.projetmobile.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Question")
data class Question(
    @PrimaryKey(autoGenerate = true) val idQuestion: Int = 0,
    @ColumnInfo(name = "questionText") val questionText: String,
    @ColumnInfo(name = "status") var status: Int = 1,
    @ColumnInfo(name = "idSubject") val idSubject: Int,
    @ColumnInfo(name = "nextRevisionDate") val nextRevisionDate: Long? = null,
    @ColumnInfo(name = "answer") val ans: Boolean = false,
)
