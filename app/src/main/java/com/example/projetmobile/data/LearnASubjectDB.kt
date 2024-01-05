package com.example.projetmobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projetmobile.data.entities.Answer
import com.example.projetmobile.data.entities.Question
import com.example.projetmobile.data.entities.Subject
import com.example.projetmobile.data.entities.SubjectQuestionAnswer

@Database(
    entities = [
        Subject::class,
        Question::class,
        Answer::class,
        SubjectQuestionAnswer::class,
    ],
    version = 44,
    exportSchema = false,
)
abstract class LearnASubjectDB : RoomDatabase() {
    abstract fun myDao(): SubjectQuestionAnswerDao

    companion object {
        @Volatile
        private var INSTANCE: LearnASubjectDB? = null

        fun getDatabase(context: Context): LearnASubjectDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LearnASubjectDB::class.java,
                    "LearnASubjectDB",
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
