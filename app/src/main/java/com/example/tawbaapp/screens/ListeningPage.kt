package com.example.tawbaapp.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.R
import com.example.tawbaapp.ViewModals.DarkModeViewModel
import com.example.tawbaapp.ui.theme.Green
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

private fun MediaPlayer?.safeIsPlaying(): Boolean {
    return try {
        this != null && this.isPlaying
    } catch (_: IllegalStateException) {
        false
    }
}

private fun MediaPlayer?.safePause() {
    try {
        if (this != null && this.isPlaying) {
            this.pause()
        }
    } catch (_: IllegalStateException) {
    }
}

private fun MediaPlayer?.safeStart() {
    try {
        this?.start()
    } catch (_: IllegalStateException) {
    }
}

private fun MediaPlayer?.safeRelease() {
    try {
        this?.reset()
    } catch (_: Exception) {
    }

    try {
        this?.release()
    } catch (_: Exception) {
    }
}

@Composable
fun ListeningPage(
    navController: NavController,
    darkModeViewModel: DarkModeViewModel,
    readerId: String
) {
    val surahs = listOf(
        Triple("الفاتحة", "Al-Fatiha", 1),
        Triple("البقرة", "Al-Baqara", 2),
        Triple("آل عمران", "Aal-E-Imran", 3),
        Triple("النساء", "An-Nisa", 4),
        Triple("المائدة", "Al-Ma'idah", 5),
        Triple("الأنعام", "Al-An'am", 6),
        Triple("الأعراف", "Al-A'raf", 7),
        Triple("الأنفال", "Al-Anfal", 8),
        Triple("التوبة", "At-Tawbah", 9),
        Triple("يونس", "Younes", 10),
        Triple("هود", "Hud", 11),
        Triple("يوسف", "Yousef", 12),
        Triple("الرعد", "Ar-Ra'd", 13),
        Triple("إبراهيم", "Ibrahim", 14),
        Triple("الحجر", "Al-Hijr", 15),
        Triple("النحل", "An-Nahl", 16),
        Triple("الإسراء", "Al-Isra", 17),
        Triple("الكهف", "Al-Kahf", 18),
        Triple("مريم", "Maryam", 19),
        Triple("طه", "Ta-Ha", 20),
        Triple("الأنبياء", "Al-Anbiya", 21),
        Triple("الحج", "Al-Hajj", 22),
        Triple("المؤمنون", "Al-Mu’minun", 23),
        Triple("النور", "An-Nur", 24),
        Triple("الفرقان", "Al-Furqan", 25),
        Triple("الشعراء", "Ash-Shu'ara", 26),
        Triple("النمل", "An-Naml", 27),
        Triple("القصص", "Al-Qasas", 28),
        Triple("العنكبوت", "Al-Ankabut", 29),
        Triple("الروم", "Ar-Rum", 30),
        Triple("لقمان", "Luqman", 31),
        Triple("السجدة", "As-Sajda", 32),
        Triple("الأحزاب", "Al-Ahzab", 33),
        Triple("سبإ", "Saba", 34),
        Triple("فاطر", "Fatir", 35),
        Triple("يس", "Ya-Sin", 36),
        Triple("الصافات", "As-Saffat", 37),
        Triple("ص", "Sad", 38),
        Triple("الزمر", "Az-Zumar", 39),
        Triple("غافر", "Ghafir", 40),
        Triple("فصلت", "Fussilat", 41),
        Triple("الشورى", "Ash-Shura", 42),
        Triple("الزخرف", "Az-Zukhruf", 43),
        Triple("الدخان", "Ad-Dukhan", 44),
        Triple("الجاثية", "Al-Jathiya", 45),
        Triple("الأحقاف", "Al-Ahqaf", 46),
        Triple("محمد", "Muhammad", 47),
        Triple("الفتح", "Al-Fath", 48),
        Triple("الحجرات", "Al-Hujurat", 49),
        Triple("ق", "Qaf", 50),
        Triple("الذاريات", "Adh-Dhariyat", 51),
        Triple("الطور", "At-Tur", 52),
        Triple("النجم", "An-Najm", 53),
        Triple("القمر", "Al-Qamar", 54),
        Triple("الرحمن", "Ar-Rahman", 55),
        Triple("الواقعة", "Al-Waqi'a", 56),
        Triple("الحديد", "Al-Hadid", 57),
        Triple("المجادلة", "Al-Mujadila", 58),
        Triple("الحشر", "Al-Hashr", 59),
        Triple("الممتحنة", "Al-Mumtahina", 60),
        Triple("الصف", "As-Saff", 61),
        Triple("الجمعة", "Al-Jumu'a", 62),
        Triple("المنافقون", "Al-Munafiqun", 63),
        Triple("التغابن", "At-Taghabun", 64),
        Triple("الطلاق", "At-Talaq", 65),
        Triple("التحريم", "At-Tahrim", 66),
        Triple("الملك", "Al-Mulk", 67),
        Triple("القلم", "Al-Qalam", 68),
        Triple("الحاقة", "Al-Haaqqa", 69),
        Triple("المعارج", "Al-Ma'arij", 70),
        Triple("نوح", "Nuh", 71),
        Triple("الجن", "Al-Jinn", 72),
        Triple("المزمل", "Al-Muzzammil", 73),
        Triple("المدثر", "Al-Muddathir", 74),
        Triple("القيامة", "Al-Qiyama", 75),
        Triple("الإنسان", "Al-Insan", 76),
        Triple("المرسلات", "Al-Mursalat", 77),
        Triple("النبأ", "An-Naba", 78),
        Triple("النازعات", "An-Nazi'at", 79),
        Triple("عبس", "Abasa", 80),
        Triple("التكوير", "At-Takwir", 81),
        Triple("الإنفطار", "Al-Infitar", 82),
        Triple("المطففين", "Al-Mutaffifin", 83),
        Triple("الإنشقاق", "Al-Inshiqaq", 84),
        Triple("البروج", "Al-Buruj", 85),
        Triple("الطارق", "At-Tariq", 86),
        Triple("الأعلى", "Al-A'la", 87),
        Triple("الغاشية", "Al-Ghashiyah", 88),
        Triple("الفجر", "Al-Fajr", 89),
        Triple("البلد", "Al-Balad", 90),
        Triple("الشمس", "Ash-Shams", 91),
        Triple("الليل", "Al-Lail", 92),
        Triple("الضحى", "Ad-Duhaa", 93),
        Triple("الشرح", "Ash-Sharh", 94),
        Triple("التين", "At-Tin", 95),
        Triple("العلق", "Al-Alaq", 96),
        Triple("القدر", "Al-Qadr", 97),
        Triple("البينة", "Al-Bayyina", 98),
        Triple("الزلزلة", "Az-Zalzalah", 99),
        Triple("العاديات", "Al-Adiyat", 100),
        Triple("القارعة", "Al-Qari'a", 101),
        Triple("التكاثر", "At-Takathur", 102),
        Triple("العصر", "Al-Asr", 103),
        Triple("الهمزة", "Al-Humazah", 104),
        Triple("الفيل", "Al-Fil", 105),
        Triple("قريش", "Quraish", 106),
        Triple("الماعون", "Al-Ma'un", 107),
        Triple("الكوثر", "Al-Kawthar", 108),
        Triple("الكافرون", "Al-Kafirun", 109),
        Triple("النصر", "An-Nasr", 110),
        Triple("المسد", "Al-Masad", 111),
        Triple("الإخلاص", "Al-Ikhlas", 112),
        Triple("الفلق", "Al-Falaq", 113),
        Triple("الناس", "An-Nas", 114)
    )

    val listState = rememberLazyListState()
    var scrollToTop by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var progress by remember { mutableFloatStateOf(0f) }
    var durationText by remember { mutableStateOf("0:00") }
    var currentText by remember { mutableStateOf("0:00") }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }

    val isDark by darkModeViewModel.isDark.collectAsState()
    val textColor = if (isDark) Color.White else Color.Black
    val bgPage = if (isDark) Color.Black else Color.White

    LaunchedEffect(scrollToTop) {
        if (scrollToTop) {
            listState.scrollToItem(0)
            scrollToTop = false
        }
    }

    fun releaseCurrentPlayer() {
        mediaPlayer.safeRelease()
        mediaPlayer = null
        isPlaying = false
        isPrepared = false
        progress = 0f
        currentText = "0:00"
        durationText = "0:00"
    }

    fun playSurah(index: Int) {
        if (index !in surahs.indices) return

        releaseCurrentPlayer()
        selectedIndex = index

        val newPlayer = MediaPlayer()
        mediaPlayer = newPlayer

        try {
            newPlayer.setDataSource(
                "https://server8.mp3quran.net/$readerId/${surahs[index].third.toString().padStart(3, '0')}.mp3"
            )

            newPlayer.setOnPreparedListener { player ->
                if (mediaPlayer !== player) return@setOnPreparedListener
                isPrepared = true
                player.start()
                isPlaying = true
            }

            newPlayer.setOnCompletionListener { player ->
                if (mediaPlayer !== player) return@setOnCompletionListener
                isPlaying = false
                isPrepared = false
                val nextIndex = if (index == surahs.lastIndex) 0 else index + 1
                playSurah(nextIndex)
                if (nextIndex == 0) scrollToTop = true
            }

            newPlayer.setOnErrorListener { player, _, _ ->
                if (mediaPlayer === player) {
                    releaseCurrentPlayer()
                } else {
                    player.safeRelease()
                }
                true
            }

            newPlayer.prepareAsync()
        } catch (_: Exception) {
            releaseCurrentPlayer()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            releaseCurrentPlayer()
        }
    }

    LaunchedEffect(mediaPlayer, selectedIndex) {
        while (isActive) {
            val player = mediaPlayer
            if (player != null && isPrepared) {
                try {
                    val total = player.duration
                    val current = player.currentPosition

                    if (total > 0) {
                        currentText = "${(current / 1000) / 60}:${((current / 1000) % 60).toString().padStart(2, '0')}"
                        durationText = "${(total / 1000) / 60}:${((total / 1000) % 60).toString().padStart(2, '0')}"
                        progress = current.toFloat() / total.toFloat()
                    }

                    isPlaying = player.safeIsPlaying()
                } catch (_: IllegalStateException) {
                    isPlaying = false
                }
            }
            delay(500.milliseconds)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgPage)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Green)
        ) {
            IconButton(
                onClick = {
                    releaseCurrentPlayer()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left_icon),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = if (selectedIndex != -1) surahs[selectedIndex].first else "اختر سورة",
                    fontFamily = FontFamily(Font(R.font.amiri_regular)),
                    fontSize = 28.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = currentText, color = Color.White, fontSize = 12.sp)
                    Text(text = durationText, color = Color.White, fontSize = 12.sp)
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .padding(top = 12.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.previous_icon),
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (selectedIndex > 0) playSurah(selectedIndex - 1)
                            }
                    )

                    Icon(
                        painter = painterResource(
                            if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon
                        ),
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                val player = mediaPlayer ?: return@clickable
                                if (!isPrepared) return@clickable

                                if (player.safeIsPlaying()) {
                                    player.safePause()
                                    isPlaying = false
                                } else {
                                    player.safeStart()
                                    isPlaying = true
                                }
                            }
                    )

                    Icon(
                        painter = painterResource(R.drawable.next_icon),
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (selectedIndex < surahs.lastIndex) playSurah(selectedIndex + 1)
                            }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            state = listState
        ) {
            itemsIndexed(surahs) { index, surah ->
                val isSelected = index == selectedIndex
                val backgroundColor = when {
                    !isSelected -> Color.Transparent
                    isDark -> Color.White.copy(alpha = 0.2f)
                    else -> Green.copy(alpha = 0.1f)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable { playSurah(index) }
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = surah.second,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.cairo_blod)),
                        color = textColor
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = surah.first,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.amiri_bold)),
                            color = Green
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(Green, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .height(24.dp)
                                .width(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = surah.third.toString(),
                                fontSize = if (surah.third > 99) 9.sp else 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}