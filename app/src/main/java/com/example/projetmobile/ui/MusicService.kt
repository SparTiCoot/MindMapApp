package com.example.projetmobile.ui

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {
    companion object {
        const val START_ACTION = "com.exemple.projetmobile.musicservice.start"
        const val STOP_ACTION = "com.exemple.projetmobile.musicservice.stop"
    }

    private var state = "idle"

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer().apply {
            setVolume(1.0F, 1.0F)
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM).build()
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START_ACTION -> {
                if (intent.data == null) return START_NOT_STICKY

                if (state == "stopped") {
                    state = "preparing"
                    mediaPlayer?.apply {
                        setOnPreparedListener {
                            it.start()
                            state = "started"
                        }
                        prepareAsync()
                        state = "prepared"
                    }
                }

                mediaPlayer?.apply {
                    setDataSource(this@MusicService, intent.data!!)
                    state = "initialized"
                    setOnPreparedListener {
                        it.start()
                        state = "started"
                    }
                    setOnCompletionListener {
                        release()
                        mediaPlayer = null
                        state = "stopped"
                    }
                    prepareAsync()
                    state = "prepared"

                }
            }

            STOP_ACTION -> {
                stopSelf()
                state = "stopped"
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}