package com.example.projetmobile.ui

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val backgroundColor = intPreferencesKey("background_color")
    private val myStore = application.dataStore

    val defaultBackgroundColor: Int = 0xFFCCCCCC.toInt() // (gris clair)
    val backgroundColorFlow = myStore.data.map { it[backgroundColor] ?: defaultBackgroundColor }

    fun getBackgroundColor(): Flow<Int> {
        return backgroundColorFlow
    }
}