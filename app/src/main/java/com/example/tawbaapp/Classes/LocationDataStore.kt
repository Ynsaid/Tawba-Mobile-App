// LocationDataStore.kt
package com.example.tawbaapp.Classes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "location")

class LocationDataStore(private val context: Context) {
    companion object {
        val CITY_KEY = stringPreferencesKey("city")
        val WILAYA_KEY = stringPreferencesKey("wilaya")
    }

    suspend fun saveLocation(city: String, wilaya: String) {
        context.dataStore.edit { prefs ->
            prefs[CITY_KEY] = city
            prefs[WILAYA_KEY] = wilaya
        }
    }

    suspend fun loadLocation(): Pair<String, String> {
        val city = context.dataStore.data.map { it[CITY_KEY] ?: "Algiers" }.first()
        val wilaya = context.dataStore.data.map { it[WILAYA_KEY] ?: "Algiers" }.first()
        return Pair(city, wilaya)
    }
}
