package com.example.projetmobile.data

import android.app.Application

class LearnASubjectApplication : Application() {
    lateinit var database: LearnASubjectDB

    override fun onCreate() {
        super.onCreate()

        database = LearnASubjectDB.getDatabase(this)
    }
}