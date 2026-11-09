package com.example.tawbaapp.ViewModals

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tawbaapp.Classes.ReadingProgressPreference
import com.example.tawbaapp.Classes.Surah
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LastReadUiState(
    val surahId: Int,
    val surahName: String,
    val ayahIndex: Int
)

class ReadingProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val readingPrefs = ReadingProgressPreference(application)

    private val _lastRead = mutableStateOf<LastReadUiState?>(null)
    val lastRead: State<LastReadUiState?> = _lastRead

    init {
        viewModelScope.launch {
            readingPrefs.readingProgressFlow.collectLatest { saved ->
                if (saved.surahId != -1 && saved.surahName.isNotEmpty()) {
                    _lastRead.value = LastReadUiState(
                        surahId = saved.surahId,
                        surahName = saved.surahName,
                        ayahIndex = saved.ayahIndex
                    )
                } else {
                    _lastRead.value = null
                }
            }
        }
    }

    fun saveLastRead(surah: Surah, ayahIndex: Int) {
        _lastRead.value = LastReadUiState(
            surahId = surah.id,
            surahName = surah.name,
            ayahIndex = ayahIndex
        )

        viewModelScope.launch {
            readingPrefs.saveLastRead(
                surahId = surah.id,
                surahName = surah.name,
                ayahIndex = ayahIndex
            )
        }
    }
}