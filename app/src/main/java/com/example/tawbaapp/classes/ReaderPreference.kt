package com.example.tawbaapp.classes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.readerDataStore by preferencesDataStore(name = "reader_prefs")

class ReaderPreference(private val context: Context) {

    companion object {
        private val READER_ID = stringPreferencesKey("reader_id")
        const val DEFAULT_READER_ID = "afs"
    }


    val readerIdFlow: Flow<String?> = context.readerDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[READER_ID] }

    suspend fun saveReaderId(readerId: String) {
        context.readerDataStore.edit { prefs ->
            prefs[READER_ID] = readerId
        }
    }


    suspend fun getSavedReaderId(): String? = readerIdFlow.first()


    suspend fun getReaderId(): String = getSavedReaderId() ?: DEFAULT_READER_ID
}