package com.example.projetmobile.ui

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val backgroundColor = intPreferencesKey("background_color")
    private val titleFontSize = intPreferencesKey("title_font_size")
    private val bodyFontSize = intPreferencesKey("body_font_size")

    private val myStore = application.dataStore

    val defaultBackgroundColor: Int = 0xFFCCCCCC.toInt() // (gris clair)
    val defaultTitleFontSize: Int = 20
    val defaultBodyFontSize: Int = 16

    val backgroundColorFlow = myStore.data.map { it[backgroundColor] ?: defaultBackgroundColor }
    val titleFontSizeFlow = myStore.data.map { it[titleFontSize] ?: defaultTitleFontSize }
    val bodyFontSizeFlow = myStore.data.map { it[bodyFontSize] ?: defaultBodyFontSize }

    fun getBackgroundColor(): Flow<Int> {
        return backgroundColorFlow
    }

    fun changeBackgroundColor(newBackgroundColor: Int) {
        viewModelScope.launch {
            myStore.edit { it[backgroundColor] = newBackgroundColor }
        }
    }

    fun changeTitleFontSize(newSize: Int) {
        viewModelScope.launch {
            myStore.edit { it[titleFontSize] = newSize }
        }
    }

    fun changeBodyFontSize(newSize: Int) {
        viewModelScope.launch {
            myStore.edit { it[bodyFontSize] = newSize }
        }
    }
}