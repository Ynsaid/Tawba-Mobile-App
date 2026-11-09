package com.example.tawbaapp.Classes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.readerDataStore by preferencesDataStore(name = "reader_prefs")

class ReaderPreference(private val context: Context) {

    companion object {
        private val READER_ID = stringPreferencesKey("reader_id")
    }

    suspend fun saveReaderId(readerId: String) {
        context.readerDataStore.edit { prefs ->
            prefs[READER_ID] = readerId
        }
    }

    suspend fun getReaderId(): String {
        return context.readerDataStore.data
            .map { prefs -> prefs[READER_ID] ?: "afs" }
            .first()
    }
}