package com.example.tawbaapp.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Green
import com.example.tawbaapp.viewmodals.DarkModeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

private data class SurahAudio(
    val arabicName: String,
    val englishName: String,
    val number: Int
)

private val surahs: List<SurahAudio> = listOf(
    SurahAudio("الفاتحة", "Al-Fatiha", 1),
    SurahAudio("البقرة", "Al-Baqara", 2),
    SurahAudio("آل عمران", "Aal-E-Imran", 3),
    SurahAudio("النساء", "An-Nisa", 4),
    SurahAudio("المائدة", "Al-Ma'idah", 5),
    SurahAudio("الأنعام", "Al-An'am", 6),
    SurahAudio("الأعراف", "Al-A'raf", 7),
    SurahAudio("الأنفال", "Al-Anfal", 8),
    SurahAudio("التوبة", "At-Tawbah", 9),
    SurahAudio("يونس", "Younes", 10),
    SurahAudio("هود", "Hud", 11),
    SurahAudio("يوسف", "Yousef", 12),
    SurahAudio("الرعد", "Ar-Ra'd", 13),
    SurahAudio("إبراهيم", "Ibrahim", 14),
    SurahAudio("الحجر", "Al-Hijr", 15),
    SurahAudio("النحل", "An-Nahl", 16),
    SurahAudio("الإسراء", "Al-Isra", 17),
    SurahAudio("الكهف", "Al-Kahf", 18),
    SurahAudio("مريم", "Maryam", 19),
    SurahAudio("طه", "Ta-Ha", 20),
    SurahAudio("الأنبياء", "Al-Anbiya", 21),
    SurahAudio("الحج", "Al-Hajj", 22),
    SurahAudio("المؤمنون", "Al-Mu’minun", 23),
    SurahAudio("النور", "An-Nur", 24),
    SurahAudio("الفرقان", "Al-Furqan", 25),
    SurahAudio("الشعراء", "Ash-Shu'ara", 26),
    SurahAudio("النمل", "An-Naml", 27),
    SurahAudio("القصص", "Al-Qasas", 28),
    SurahAudio("العنكبوت", "Al-Ankabut", 29),
    SurahAudio("الروم", "Ar-Rum", 30),
    SurahAudio("لقمان", "Luqman", 31),
    SurahAudio("السجدة", "As-Sajda", 32),
    SurahAudio("الأحزاب", "Al-Ahzab", 33),
    SurahAudio("سبإ", "Saba", 34),
    SurahAudio("فاطر", "Fatir", 35),
    SurahAudio("يس", "Ya-Sin", 36),
    SurahAudio("الصافات", "As-Saffat", 37),
    SurahAudio("ص", "Sad", 38),
    SurahAudio("الزمر", "Az-Zumar", 39),
    SurahAudio("غافر", "Ghafir", 40),
    SurahAudio("فصلت", "Fussilat", 41),
    SurahAudio("الشورى", "Ash-Shura", 42),
    SurahAudio("الزخرف", "Az-Zukhruf", 43),
    SurahAudio("الدخان", "Ad-Dukhan", 44),
    SurahAudio("الجاثية", "Al-Jathiya", 45),
    SurahAudio("الأحقاف", "Al-Ahqaf", 46),
    SurahAudio("محمد", "Muhammad", 47),
    SurahAudio("الفتح", "Al-Fath", 48),
    SurahAudio("الحجرات", "Al-Hujurat", 49),
    SurahAudio("ق", "Qaf", 50),
    SurahAudio("الذاريات", "Adh-Dhariyat", 51),
    SurahAudio("الطور", "At-Tur", 52),
    SurahAudio("النجم", "An-Najm", 53),
    SurahAudio("القمر", "Al-Qamar", 54),
    SurahAudio("الرحمن", "Ar-Rahman", 55),
    SurahAudio("الواقعة", "Al-Waqi'a", 56),
    SurahAudio("الحديد", "Al-Hadid", 57),
    SurahAudio("المجادلة", "Al-Mujadila", 58),
    SurahAudio("الحشر", "Al-Hashr", 59),
    SurahAudio("الممتحنة", "Al-Mumtahina", 60),
    SurahAudio("الصف", "As-Saff", 61),
    SurahAudio("الجمعة", "Al-Jumu'a", 62),
    SurahAudio("المنافقون", "Al-Munafiqun", 63),
    SurahAudio("التغابن", "At-Taghabun", 64),
    SurahAudio("الطلاق", "At-Talaq", 65),
    SurahAudio("التحريم", "At-Tahrim", 66),
    SurahAudio("الملك", "Al-Mulk", 67),
    SurahAudio("القلم", "Al-Qalam", 68),
    SurahAudio("الحاقة", "Al-Haaqqa", 69),
    SurahAudio("المعارج", "Al-Ma'arij", 70),
    SurahAudio("نوح", "Nuh", 71),
    SurahAudio("الجن", "Al-Jinn", 72),
    SurahAudio("المزمل", "Al-Muzzammil", 73),
    SurahAudio("المدثر", "Al-Muddathir", 74),
    SurahAudio("القيامة", "Al-Qiyama", 75),
    SurahAudio("الإنسان", "Al-Insan", 76),
    SurahAudio("المرسلات", "Al-Mursalat", 77),
    SurahAudio("النبأ", "An-Naba", 78),
    SurahAudio("النازعات", "An-Nazi'at", 79),
    SurahAudio("عبس", "Abasa", 80),
    SurahAudio("التكوير", "At-Takwir", 81),
    SurahAudio("الإنفطار", "Al-Infitar", 82),
    SurahAudio("المطففين", "Al-Mutaffifin", 83),
    SurahAudio("الإنشقاق", "Al-Inshiqaq", 84),
    SurahAudio("البروج", "Al-Buruj", 85),
    SurahAudio("الطارق", "At-Tariq", 86),
    SurahAudio("الأعلى", "Al-A'la", 87),
    SurahAudio("الغاشية", "Al-Ghashiyah", 88),
    SurahAudio("الفجر", "Al-Fajr", 89),
    SurahAudio("البلد", "Al-Balad", 90),
    SurahAudio("الشمس", "Ash-Shams", 91),
    SurahAudio("الليل", "Al-Lail", 92),
    SurahAudio("الضحى", "Ad-Duhaa", 93),
    SurahAudio("الشرح", "Ash-Sharh", 94),
    SurahAudio("التين", "At-Tin", 95),
    SurahAudio("العلق", "Al-Alaq", 96),
    SurahAudio("القدر", "Al-Qadr", 97),
    SurahAudio("البينة", "Al-Bayyina", 98),
    SurahAudio("الزلزلة", "Az-Zalzalah", 99),
    SurahAudio("العاديات", "Al-Adiyat", 100),
    SurahAudio("القارعة", "Al-Qari'a", 101),
    SurahAudio("التكاثر", "At-Takathur", 102),
    SurahAudio("العصر", "Al-Asr", 103),
    SurahAudio("الهمزة", "Al-Humazah", 104),
    SurahAudio("الفيل", "Al-Fil", 105),
    SurahAudio("قريش", "Quraish", 106),
    SurahAudio("الماعون", "Al-Ma'un", 107),
    SurahAudio("الكوثر", "Al-Kawthar", 108),
    SurahAudio("الكافرون", "Al-Kafirun", 109),
    SurahAudio("النصر", "An-Nasr", 110),
    SurahAudio("المسد", "Al-Masad", 111),
    SurahAudio("الإخلاص", "Al-Ikhlas", 112),
    SurahAudio("الفلق", "Al-Falaq", 113),
    SurahAudio("الناس", "An-Nas", 114)
)

private fun MediaPlayer?.safeIsPlaying(): Boolean {
    return try {
        this != null && isPlaying
    } catch (_: IllegalStateException) {
        false
    }
}

private fun MediaPlayer?.safePause() {
    try {
        if (this != null && isPlaying) {
            pause()
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

private fun MediaPlayer?.safeSeekTo(position: Int) {
    try {
        this?.seekTo(position)
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

private fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false

    val capabilities = connectivityManager.getNetworkCapabilities(network)
        ?: return false

    return capabilities.hasCapability(
        NetworkCapabilities.NET_CAPABILITY_INTERNET
    )
}

private fun formatTime(milliseconds: Int): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Composable
fun ListeningPage(
    navController: NavController,
    darkModeViewModel: DarkModeViewModel,
    readerId: String
) {
    val context = LocalContext.current

    val listState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var isLoadingAudio by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var currentText by remember { mutableStateOf("0:00") }
    var durationText by remember { mutableStateOf("0:00") }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var audioError by remember { mutableStateOf<String?>(null) }
    var showNoInternet by remember { mutableStateOf(false) }
    var scrollToTop by remember { mutableStateOf(false) }

    val isDark by darkModeViewModel.isDark.collectAsState()

    val textColor = if (isDark) Color.White else Color.Black
    val pageBackground = if (isDark) Color(0xFF101410) else Color.White
    val cardBackground = if (isDark) Color(0xFF1B211B) else Color(0xFFF5F7F3)
    val mutedColor = if (isDark) Color(0xFFA8B5A9) else Color(0xFF6B756C)

    val filteredSurahs = remember(searchQuery) {
        val query = searchQuery.trim()

        if (query.isBlank()) {
            surahs
        } else {
            surahs.filter { surah ->
                surah.arabicName.contains(query, ignoreCase = true) ||
                        surah.englishName.contains(query, ignoreCase = true) ||
                        surah.number.toString().contains(query)
            }
        }
    }

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
        isLoadingAudio = false
        progress = 0f
        currentText = "0:00"
        durationText = "0:00"
    }

    fun playSurah(index: Int) {
        if (index !in surahs.indices) return

        if (!isInternetAvailable(context)) {
            showNoInternet = true
            return
        }

        releaseCurrentPlayer()
        selectedIndex = index
        isLoadingAudio = true
        audioError = null

        val audioUrl =
            "https://server8.mp3quran.net/$readerId/" +
                    surahs[index].number
                        .toString()
                        .padStart(3, '0') +
                    ".mp3"

        val newPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

        mediaPlayer = newPlayer

        try {
            newPlayer.setDataSource(audioUrl)

            newPlayer.setOnPreparedListener { player ->
                if (mediaPlayer !== player) {
                    player.safeRelease()
                    return@setOnPreparedListener
                }

                isPrepared = true
                isLoadingAudio = false
                player.safeStart()
                isPlaying = true
            }

            newPlayer.setOnCompletionListener { player ->
                if (mediaPlayer !== player) {
                    player.safeRelease()
                    return@setOnCompletionListener
                }

                isPlaying = false
                isPrepared = false

                val nextIndex =
                    if (index == surahs.lastIndex) {
                        0
                    } else {
                        index + 1
                    }

                playSurah(nextIndex)

                if (nextIndex == 0) {
                    scrollToTop = true
                }
            }

            newPlayer.setOnErrorListener { player, _, _ ->
                if (mediaPlayer === player) {
                    audioError =
                        "تعذر تشغيل التلاوة. تحقق من اتصال الإنترنت وحاول مجددًا."
                    releaseCurrentPlayer()
                } else {
                    player.safeRelease()
                }

                true
            }

            newPlayer.prepareAsync()
        } catch (_: Exception) {
            audioError =
                "تعذر تشغيل التلاوة. تحقق من اتصال الإنترنت وحاول مجددًا."
            releaseCurrentPlayer()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            releaseCurrentPlayer()
        }
    }

    LaunchedEffect(mediaPlayer, isPrepared) {
        while (isActive) {
            val player = mediaPlayer

            if (player != null && isPrepared) {
                try {
                    val total = player.duration
                    val current = player.currentPosition

                    if (total > 0) {
                        currentText = formatTime(current)
                        durationText = formatTime(total)
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
            .background(pageBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    releaseCurrentPlayer()
                    navController.popBackStack()
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.arrow_left_icon
                    ),
                    contentDescription = "رجوع",
                    tint = if (isDark) {
                        Color.White
                    } else {
                        Color.Black
                    }
                )
            }

            Text(
                text = "الاستماع إلى القرآن",
                fontFamily = FontFamily(
                    Font(R.font.amiri_regular)
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) {
                    Color.White
                } else {
                    Color.Black
                },
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Green)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Text(
                    text = if (selectedIndex != -1) {
                        surahs[selectedIndex].arabicName
                    } else {
                        "اختر سورة"
                    },
                    fontFamily = FontFamily(
                        Font(R.font.amiri_regular)
                    ),
                    fontSize = 26.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentText,
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    Text(
                        text = durationText,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                Slider(
                    value = progress,
                    onValueChange = { value ->
                        progress = value

                        val player = mediaPlayer

                        if (player != null && isPrepared) {
                            val newPosition =
                                (value * player.duration).toInt()

                            player.safeSeekTo(newPosition)
                            currentText = formatTime(newPosition)
                        }
                    },
                    enabled = isPrepared,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.previous_icon
                        ),
                        contentDescription = "السابقة",
                        tint = Color.White,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable {
                                if (selectedIndex > 0) {
                                    playSurah(selectedIndex - 1)
                                }
                            }
                    )

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(alpha = 0.18f)
                            )
                            .clickable {
                                val player = mediaPlayer

                                if (player == null || !isPrepared) {
                                    return@clickable
                                }

                                if (player.safeIsPlaying()) {
                                    player.safePause()
                                    isPlaying = false
                                } else {
                                    player.safeStart()
                                    isPlaying = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoadingAudio) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painter = painterResource(
                                    if (isPlaying) {
                                        R.drawable.pause_icon
                                    } else {
                                        R.drawable.play_icon
                                    }
                                ),
                                contentDescription =
                                    if (isPlaying) "إيقاف" else "تشغيل",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Icon(
                        painter = painterResource(
                            R.drawable.next_icon
                        ),
                        contentDescription = "التالية",
                        tint = Color.White,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable {
                                if (selectedIndex < surahs.lastIndex) {
                                    playSurah(selectedIndex + 1)
                                }
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Rtl
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    placeholder = {
                        Text(
                            text = "ابحث عن سورة...",
                            color = mutedColor,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                R.drawable.dots_icon
                            ),
                            contentDescription = "بحث",
                            tint = Green
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                }
                            ) {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.dots_icon
                                    ),
                                    contentDescription = "مسح البحث",
                                    tint = mutedColor
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBackground,
                        unfocusedContainerColor = cardBackground,
                        focusedBorderColor = Green,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Green,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
            }

            if (audioError != null) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = audioError.orEmpty(),
                    color = Color(0xFFB54E46),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (filteredSurahs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد نتائج مطابقة للبحث.",
                    color = mutedColor,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(filteredSurahs) { _, surah ->
                    val actualIndex =
                        surahs.indexOfFirst {
                            it.number == surah.number
                        }

                    val isSelected = actualIndex == selectedIndex

                    val itemBackground = when {
                        isSelected -> Green.copy(alpha = 0.14f)
                        else -> cardBackground
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(itemBackground)
                            .clickable {
                                playSurah(actualIndex)
                            }
                            .padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = surah.arabicName,
                                fontSize = 19.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.amiri_bold)
                                ),
                                color = if (isSelected) {
                                    Green
                                } else {
                                    textColor
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = surah.englishName,
                                fontSize = 12.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.cairo_blod)
                                ),
                                color = mutedColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    Green,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 9.dp,
                                    vertical = 4.dp
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = surah.number.toString(),
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNoInternet) {
        AlertDialog(
            onDismissRequest = {
                showNoInternet = false
            },
            title = {
                Text(
                    text = "لا يوجد اتصال بالإنترنت",
                    fontFamily = FontFamily(
                        Font(R.font.cairo_blod)
                    )
                )
            },
            text = {
                Text(
                    text = "تحتاج إلى اتصال بالإنترنت لتشغيل التلاوة الصوتية. تحقق من شبكتك ثم أعد المحاولة.",
                    color = mutedColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNoInternet = false

                        if (selectedIndex != -1) {
                            playSurah(selectedIndex)
                        }
                    }
                ) {
                    Text(
                        text = "إعادة المحاولة",
                        color = Green
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNoInternet = false
                    }
                ) {
                    Text(
                        text = "إغلاق",
                        color = mutedColor
                    )
                }
            }
        )
    }
}