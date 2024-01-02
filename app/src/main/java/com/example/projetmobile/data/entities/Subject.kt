package com.example.projetmobile.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Subject")
data class Subject(
    @PrimaryKey(autoGenerate = true) val idSubject: Int = 0,
    val name: String,
)
