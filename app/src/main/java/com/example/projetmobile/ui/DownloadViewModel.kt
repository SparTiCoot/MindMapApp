package com.example.projetmobile.ui

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetmobile.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class DownloadViewModel(private val application: Application) : AndroidViewModel(application) {
    private val fileNames: Array<String> = application.resources.getStringArray(R.array.nom_fichier)
    private val fileUrls: Array<String> = application.resources.getStringArray(R.array.fichier_url)

    private var myJSON: JSONObject? = null
    private var idLoad = 0L

    private val downloadManager =
        application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val idDownload: Long = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
            if (idDownload == idLoad) {
                createJson("${fileNames[0]}.json")
            }
        }
    }
    private val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

    init {
        application.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
    }

    fun doDownload(myViewModel: MyViewModel, snackbarHostState: SnackbarHostState) {
        val fileName = fileNames[0]
        val fileUrl = fileUrls[0]

        startDownload(fileUrl, "${fileName}.json")
        getJSONAndInsertData(myViewModel, snackbarHostState)
    }

    private fun startDownload(adr: String, fName: String) {
        val uri = Uri.parse(adr)
        val request = DownloadManager.Request(uri)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(
                application, Environment.DIRECTORY_DOCUMENTS, fName
            )
        idLoad = downloadManager.enqueue(request)
    }

    private fun createJson(fName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(
                application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fName
            )
            val jsonString = file.readText()

            try {
                myJSON = JSONObject(jsonString)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun getJSON(): JSONObject? {
        return myJSON
    }

    private fun getJSONAndInsertData(
        myViewModel: MyViewModel, snackbarHostState: SnackbarHostState
    ) {
        viewModelScope.launch {
            while (getJSON() == null) {
                delay(100)
            }
            getJSON()?.let {
                myViewModel.insertDataFromJson(it, snackbarHostState)
            }
        }
    }
}