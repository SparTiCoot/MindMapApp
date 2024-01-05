package com.example.projetmobile.ui

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.projetmobile.components.createNotif

class RappelWorker (private val context : Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
        override fun doWork(): Result {
            createNotif(context)
            return Result.success()
        }
}