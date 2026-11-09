package com.example.tawbaapp.Classes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.readingProgressDataStore by preferencesDataStore(name = "reading_progress_prefs")

data class SavedReadingProgress(
    val surahId: Int = -1,
    val surahName: String = "",
    val ayahIndex: Int = 0
)

class ReadingProgressPreference(private val context: Context) {

    companion object {
        private val SURAH_ID = intPreferencesKey("surah_id")
        private val SURAH_NAME = stringPreferencesKey("surah_name")
        private val AYAH_INDEX = intPreferencesKey("ayah_index")
    }

    val readingProgressFlow: Flow<SavedReadingProgress> =
        context.readingProgressDataStore.data.map { prefs ->
            SavedReadingProgress(
                surahId = prefs[SURAH_ID] ?: -1,
                surahName = prefs[SURAH_NAME] ?: "",
                ayahIndex = prefs[AYAH_INDEX] ?: 0
            )
        }

    suspend fun saveLastRead(
        surahId: Int,
        surahName: String,
        ayahIndex: Int
    ) {
        context.readingProgressDataStore.edit { prefs ->
            prefs[SURAH_ID] = surahId
            prefs[SURAH_NAME] = surahName
            prefs[AYAH_INDEX] = ayahIndex
        }
    }
}