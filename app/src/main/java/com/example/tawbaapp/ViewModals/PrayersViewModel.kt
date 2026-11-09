package com.example.tawbaapp.ViewModals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tawbaapp.Classes.PrayerTimes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PrayerViewModel : ViewModel() {
    private val _prayerTimes = MutableStateFlow<PrayerTimes?>(null)
    val prayerTimes: StateFlow<PrayerTimes?> = _prayerTimes

    fun fetchPrayerTimes(city: String = "Algiers", country: String = "DZ") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiUrl =
                    "https://api.aladhan.com/v1/timingsByCity?city=$city&country=$country&method=2"
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val inputStream = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(inputStream)
                val timings = json.getJSONObject("data").getJSONObject("timings")

                val times = PrayerTimes(
                    fajr = timings.getString("Fajr"),
                    dhuhr = timings.getString("Dhuhr"),
                    asr = timings.getString("Asr"),
                    maghrib = timings.getString("Maghrib"),
                    isha = timings.getString("Isha")
                )

                _prayerTimes.value = times
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
