package com.example.tawbaapp.viewmodals

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tawbaapp.Classes.PrayerTimes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.core.content.edit

data class PrayerDayData(
    val date: LocalDate,
    val times: PrayerTimes,
    val sunrise: String,
    val zoneId: String,
    val methodName: String,
)

data class PrayerUiState(
    val isLoading: Boolean = false,
    val locationLabel: String = "الجزائر العاصمة",
    val isGpsLocation: Boolean = false,
    val today: PrayerDayData? = null,
    val tomorrow: PrayerDayData? = null,
    val error: String? = null,
)

private data class PrayerLocationChoice(
    val city: String = "Algiers",
    val country: String = "DZ",
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    val usesCoordinates: Boolean
        get() = latitude != null && longitude != null

    val label: String
        get() = when {
            usesCoordinates -> "الموقع المحدد من الجهاز"

            city.equals("Algiers", true) &&
                    country.equals("DZ", true) -> {
                "الجزائر العاصمة"
            }

            else -> city
        }
}

@RequiresApi(Build.VERSION_CODES.O)
class PrayerViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val preferences = application.getSharedPreferences(
        "prayer_location_v2",
        Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()


    private val _prayerTimes = MutableStateFlow<PrayerTimes?>(null)


    private var activeLocation = readSavedLocation()
    private var requestJob: Job? = null

    init {
        load(activeLocation)
    }

    fun fetchPrayerTimes(
        city: String = "Algiers",
        country: String = "DZ",
    ) {
        val cleanCity = city.trim()
        val cleanCountry = country.trim()

        if (cleanCity.isBlank() || cleanCountry.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "أدخل اسم المدينة والدولة."
            )
            return
        }

        load(
            PrayerLocationChoice(
                city = cleanCity,
                country = cleanCountry
            )
        )
    }

    fun fetchPrayerTimesByCoordinates(
        latitude: Double,
        longitude: Double,
    ) {
        if (
            !latitude.isFinite() ||
            !longitude.isFinite() ||
            latitude !in -90.0..90.0 ||
            longitude !in -180.0..180.0
        ) {
            _uiState.value = _uiState.value.copy(
                error = "إحداثيات الموقع غير صحيحة."
            )
            return
        }

        load(
            PrayerLocationChoice(
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    fun useDefaultLocation() {
        fetchPrayerTimes(
            city = "Algiers",
            country = "DZ"
        )
    }

    fun refresh() {
        load(activeLocation)
    }

    fun refreshIfDateChanged() {
        val today = _uiState.value.today ?: return

        if (_uiState.value.isLoading) return

        val zone = runCatching {
            ZoneId.of(today.zoneId)
        }.getOrDefault(ZoneId.of("Africa/Algiers"))

        if (today.date != LocalDate.now(zone)) {
            refresh()
        }
    }

    private fun load(location: PrayerLocationChoice) {
        requestJob?.cancel()
        activeLocation = location

        // لا نعرض مواقيت موقع سابق تحت اسم موقع جديد.
        _prayerTimes.value = null

        _uiState.value = PrayerUiState(
            isLoading = true,
            locationLabel = location.label,
            isGpsLocation = location.usesCoordinates
        )

        requestJob = viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    var date = LocalDate.now(
                        ZoneId.of("Africa/Algiers")
                    )

                    var today = requestDay(
                        location = location,
                        date = date
                    )

                    currentCoroutineContext().ensureActive()

                    // نتأكد أن تاريخ الطلب مطابق لتاريخ الموقع.
                    val locationDate = LocalDate.now(
                        ZoneId.of(today.zoneId)
                    )

                    if (locationDate != date) {
                        date = locationDate

                        today = requestDay(
                            location = location,
                            date = date
                        )
                    }

                    currentCoroutineContext().ensureActive()

                    val tomorrow = requestDay(
                        location = location,
                        date = date.plusDays(1)
                    )

                    currentCoroutineContext().ensureActive()

                    today to tomorrow
                }

                val today = result.first
                val tomorrow = result.second

                saveLocation(location)

                _prayerTimes.value = today.times

                _uiState.value = PrayerUiState(
                    locationLabel = location.label,
                    isGpsLocation = location.usesCoordinates,
                    today = today,
                    tomorrow = tomorrow
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.value = PrayerUiState(
                    locationLabel = location.label,
                    isGpsLocation = location.usesCoordinates,
                    error = "تعذر جلب المواقيت. تحقق من الاتصال واسم المدينة ثم أعد المحاولة."
                )
            }
        }
    }

    private fun requestDay(
        location: PrayerLocationChoice,
        date: LocalDate,
    ): PrayerDayData {
        val formattedDate = date.format(
            DateTimeFormatter.ofPattern(
                "dd-MM-yyyy",
                Locale.US
            )
        )

        val parameters = linkedMapOf(
            "method" to "19"
        )

        val endpoint = if (location.usesCoordinates) {
            parameters["latitude"] =
                location.latitude.toString()

            parameters["longitude"] =
                location.longitude.toString()

            "timings"
        } else {
            parameters["city"] = location.city
            parameters["country"] = location.country

            "timingsByCity"
        }

        val query = parameters.entries.joinToString("&") {
            "${encode(it.key)}=${encode(it.value)}"
        }

        val url = URL(
            "https://api.aladhan.com/v1/" +
                    "$endpoint/$formattedDate?$query"
        )

        val connection =
            url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            val statusCode = connection.responseCode

            if (statusCode !in 200..299) {
                throw IllegalStateException(
                    "HTTP $statusCode"
                )
            }

            val body = connection.inputStream
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val root = JSONObject(body)

            if (root.optInt("code") != 200) {
                throw IllegalStateException(
                    root.optString("status")
                )
            }

            val data = root.getJSONObject("data")
            val timings = data.getJSONObject("timings")
            val metadata = data.getJSONObject("meta")

            val zone = metadata.getString("timezone")

            // التحقق مبكرًا من المنطقة الزمنية.
            ZoneId.of(zone)

            return PrayerDayData(
                date = date,
                times = PrayerTimes(
                    fajr = cleanTime(
                        timings.getString("Fajr")
                    ),
                    dhuhr = cleanTime(
                        timings.getString("Dhuhr")
                    ),
                    asr = cleanTime(
                        timings.getString("Asr")
                    ),
                    maghrib = cleanTime(
                        timings.getString("Maghrib")
                    ),
                    isha = cleanTime(
                        timings.getString("Isha")
                    )
                ),
                sunrise = cleanTime(
                    timings.getString("Sunrise")
                ),
                zoneId = zone,
                methodName = metadata
                    .getJSONObject("method")
                    .getString("name")
            )
        } finally {
            connection.disconnect()
        }
    }

    private fun cleanTime(raw: String): String {
        val value = Regex("""\d{1,2}:\d{2}""")
            .find(raw)
            ?.value
            ?: throw IllegalStateException(
                "Invalid prayer time"
            )

        val parsed = LocalTime.parse(
            value,
            DateTimeFormatter.ofPattern(
                "H:mm",
                Locale.US
            )
        )

        return parsed.format(
            DateTimeFormatter.ofPattern(
                "HH:mm",
                Locale.US
            )
        )
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
    }

    private fun saveLocation(
        location: PrayerLocationChoice,
    ) {
        preferences.edit {
            putBoolean(
                "uses_coordinates",
                location.usesCoordinates
            )

            putString("city", location.city)
            putString("country", location.country)

            putString(
                "latitude",
                location.latitude?.toString()
            )

            putString(
                "longitude",
                location.longitude?.toString()
            )
        }
    }

    private fun readSavedLocation(): PrayerLocationChoice {
        if (
            preferences.getBoolean(
                "uses_coordinates",
                false
            )
        ) {
            val latitude = preferences
                .getString("latitude", null)
                ?.toDoubleOrNull()

            val longitude = preferences
                .getString("longitude", null)
                ?.toDoubleOrNull()

            if (
                latitude != null &&
                longitude != null &&
                latitude.isFinite() &&
                longitude.isFinite() &&
                latitude in -90.0..90.0 &&
                longitude in -180.0..180.0
            ) {
                return PrayerLocationChoice(
                    latitude = latitude,
                    longitude = longitude
                )
            }
        }

        return PrayerLocationChoice(
            city = preferences.getString(
                "city",
                "Algiers"
            )?.takeIf { it.isNotBlank() } ?: "Algiers",

            country = preferences.getString(
                "country",
                "DZ"
            )?.takeIf { it.isNotBlank() } ?: "DZ"
        )
    }
}