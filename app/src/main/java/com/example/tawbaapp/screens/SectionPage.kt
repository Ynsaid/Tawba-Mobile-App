package com.example.tawbaapp.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.classes.ReaderPreference
import com.example.tawbaapp.classes.Surah
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Green
import com.example.tawbaapp.viewmodals.DarkModeViewModel
import com.example.tawbaapp.viewmodals.PrayerViewModel
import com.example.tawbaapp.viewmodals.ReadingProgressViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private data class HomeReader(
    val name: String,
    val id: String
)

private data class HomePrayer(
    val name: String,
    val time: String,
    val parsedTime: LocalTime?
)

private data class HomeColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val secondary: Color,
    val accent: Color,
    val hero: Color,
    val reading: Color,
    val listening: Color,
    val prayers: Color,
    val adhkar: Color
)

private val homeReaders = listOf(
    HomeReader("العفاسي", "afs"),
    HomeReader("أيوب بن محمد بن أحمد", "ayyub"),
    HomeReader("أحمد الحذيفي", "ahmad_huth"),
    HomeReader("إبراهيم الأخضر", "bna"),
    HomeReader("البرمي", "brmi"),
    HomeReader("بندر بليلة", "bu_khtr"),
    HomeReader("أبو عبدالله جابر", "buajan"),
    HomeReader("الدليمي", "dlami"),
    HomeReader("فهد العتيبي", "fahad_otibi"),
    HomeReader("فواز السعيد", "fawaz"),
    HomeReader("فارس عباد", "frs_a")
)

private val homeSearchMarks = Regex(
    "[\\u064B-\\u065F\\u0670\\u0640]"
)

private fun normalizeHomeSearch(value: String): String {
    return value
        .replace(homeSearchMarks, "")
        .replace('أ', 'ا')
        .replace('إ', 'ا')
        .replace('آ', 'ا')
        .replace('ى', 'ي')
        .trim()
        .lowercase()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SectionPage(
    navController: NavController,
    darkModeViewModel: DarkModeViewModel,
    readingProgressViewModel: ReadingProgressViewModel,
    surahList: List<Surah>,
    prayerViewModel: PrayerViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val isDark by darkModeViewModel.isDark.collectAsState()
    val lastRead by readingProgressViewModel.lastRead

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val snackbarState = remember {
        SnackbarHostState()
    }

    val readerPreference = remember(context.applicationContext) {
        ReaderPreference(context.applicationContext)
    }

    val gson = remember {
        Gson()
    }

    val cairoBold = remember {
        FontFamily(Font(R.font.cairo_blod))
    }

    val amiriRegular = remember {
        FontFamily(Font(R.font.amiri_regular))
    }

    var showReaderSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedReaderId by rememberSaveable {
        mutableStateOf(homeReaders.first().id)
    }

    val selectedReader = remember(selectedReaderId) {
        homeReaders.firstOrNull {
            it.id == selectedReaderId
        } ?: homeReaders.first()
    }

    val savedSurahId = lastRead?.surahId

    val resumeSurah = remember(surahList, savedSurahId) {
        surahList.firstOrNull {
            it.id == savedSurahId
        }
    }

    val colors = remember(isDark) {
        if (isDark) {
            HomeColors(
                background = Color(0xFF0E1410),
                surface = Color(0xFF18221C),
                text = Color(0xFFE7EFE9),
                secondary = Color(0xFFA2B2A7),
                accent = Color(0xFF76D6A0),
                hero = Color(0xFF185D3D),
                reading = Color(0xFF192D38),
                listening = Color(0xFF322333),
                prayers = Color(0xFF1B3328),
                adhkar = Color(0xFF35301F)
            )
        } else {
            HomeColors(
                background = Color(0xFFF6F8F5),
                surface = Color.White,
                text = Color(0xFF17251D),
                secondary = Color(0xFF66766C),
                accent = Green,
                hero = Green,
                reading = Color(0xFFEAF2FA),
                listening = Color(0xFFF7EAF1),
                prayers = Color(0xFFE7F3EA),
                adhkar = Color(0xFFFAF2DA)
            )
        }
    }

    Scaffold(
        containerColor = colors.background,
        snackbarHost = {
            SnackbarHost(snackbarState)
        }
    ) { scaffoldPadding ->
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(scrollState)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(20.dp)
            ) {
                HomeTopBar(
                    isDark = isDark,
                    colors = colors,
                    font = cairoBold,
                    onReaderClick = {
                        showReaderSheet = true
                    },
                    onThemeClick = {
                        darkModeViewModel.toggleTheme()
                    }
                )
                HomePrayerSection(
                    viewModel = prayerViewModel,
                    colors = colors,
                    font = cairoBold,
                    onOpenPrayers = {
                        navController.navigate("prayers") {
                            launchSingleTop = true
                        }
                    }
                )

                HomeReadingCard(
                    hasSavedReading = lastRead != null,
                    canResume = resumeSurah != null,
                    surahName = lastRead?.surahName,
                    ayahNumber = lastRead?.ayahIndex?.plus(1),
                    colors = colors,
                    titleFont = cairoBold,
                    quranFont = amiriRegular,
                    onContinue = {

                        if (resumeSurah != null) {
                            val json = URLEncoder.encode(
                                gson.toJson(resumeSurah),
                                "UTF-8"
                            )

                            navController.navigate(
                                "surah/$json"
                            ) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate("reading") {
                                launchSingleTop = true
                            }
                        }
                    }
                )

                Text(
                    text = "الأقسام",
                    color = colors.text,
                    fontFamily = cairoBold,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        HomeSectionCard(
                            title = "القراءة",
                            subtitle = "تصفح سور القرآن",
                            imageRes = R.drawable.quran_svg_iconv2,
                            backgroundColor = colors.reading,
                            textColor = colors.text,
                            secondaryColor = colors.secondary,
                            font = cairoBold,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate("reading") {
                                    launchSingleTop = true
                                }
                            }
                        )

                        HomeSectionCard(
                            title = "الاستماع",
                            subtitle = "اختر قارئك المفضل",
                            imageRes = R.drawable.listeninig_svg_icon,
                            backgroundColor = colors.listening,
                            textColor = colors.text,
                            secondaryColor = colors.secondary,
                            font = cairoBold,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                showReaderSheet = true
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        HomeSectionCard(
                            title = "مواقيت الصلاة",
                            subtitle = "اعرف موعد الصلاة",
                            imageRes = R.drawable.pray_svg_icon,
                            backgroundColor = colors.prayers,
                            textColor = colors.text,
                            secondaryColor = colors.secondary,
                            font = cairoBold,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate("prayers") {
                                    launchSingleTop = true
                                }
                            }
                        )

                        HomeSectionCard(
                            title = "الأذكار",
                            subtitle = "لحظات من الذكر",
                            imageRes = R.drawable.sebha_svg_icon,
                            backgroundColor = colors.adhkar,
                            textColor = colors.text,
                            secondaryColor = colors.secondary,
                            font = cairoBold,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate("adhkars") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showReaderSheet) {
        HomeReaderSheet(
            selectedReaderId = selectedReader.id,
            colors = colors,
            font = cairoBold,
            onDismiss = {
                showReaderSheet = false
            },
            onConfirm = { reader ->
                selectedReaderId = reader.id

                scope.launch {
                    try {
                        readerPreference.saveReaderId(reader.id)
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        snackbarState.showSnackbar(
                            "تعذر حفظ القارئ المفضل."
                        )
                    }
                }

                showReaderSheet = false

                navController.navigate(
                    "listing/${reader.id}"
                ) {
                    launchSingleTop = true
                }
            }
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HomePrayerSection(
    viewModel: PrayerViewModel,
    colors: HomeColors,
    font: FontFamily,
    onOpenPrayers: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val zone = remember(state.today?.zoneId) {
        runCatching {
            ZoneId.of(
                state.today?.zoneId ?: "Africa/Algiers"
            )
        }.getOrDefault(
            ZoneId.of("Africa/Algiers")
        )
    }

    var now by remember(zone) {
        mutableStateOf(ZonedDateTime.now(zone))
    }

    // تحديث هذا القسم عند بداية كل دقيقة،
    // دون مؤقت يغيّر حالة الصفحة الرئيسية كلها.
    LaunchedEffect(zone, viewModel) {
        while (isActive) {
            val current = ZonedDateTime.now(zone)
            now = current

            viewModel.refreshIfDateChanged()

            val nextMinute = current
                .plusMinutes(1)
                .withSecond(0)
                .withNano(0)

            delay(
                Duration.between(
                    current,
                    nextMinute
                ).toMillis().coerceAtLeast(250L).milliseconds
            )
        }
    }

    val prayers = remember(state.today) {
        val times = state.today?.times

        listOf(
            "الفجر" to times?.fajr,
            "الظهر" to times?.dhuhr,
            "العصر" to times?.asr,
            "المغرب" to times?.maghrib,
            "العشاء" to times?.isha
        ).map { (name, value) ->
            HomePrayer(
                name = name,
                time = value ?: "--:--",
                parsedTime = value?.let {
                    runCatching {
                        LocalTime.parse(it)
                    }.getOrNull()
                }
            )
        }
    }

    val currentPrayerName = remember(
        prayers,
        now,
        state.today?.date
    ) {
        if (state.today?.date != now.toLocalDate()) {
            null
        } else {
            val validPrayers = prayers
                .filter { it.parsedTime != null }
                .sortedBy { it.parsedTime }

            validPrayers.lastOrNull {
                !it.parsedTime!!.isAfter(now.toLocalTime())
            }?.name
            // قبل الفجر: آخر موعد أذان هو العشاء.
                ?: validPrayers.lastOrNull()?.name
        }
    }

    Column(
        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "صلوات اليوم",
                    color = colors.text,
                    fontFamily = font,
                    fontSize = 18.sp
                )
            }
            TextButton(
                onClick = onOpenPrayers
            ) {
                Text(
                    text = "التفاصيل",
                    color = colors.accent,
                    fontFamily = font,
                    fontSize = 11.sp
                )
            }
        }

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = colors.accent,
                trackColor = colors.surface
            )
        }
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val spacing = 8.dp

            val cardWidth = (
                    (maxWidth - spacing * 4) / 5f
                    ).coerceAtLeast(68.dp)

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(spacing)
            ) {
                items(
                    items = prayers,
                    key = { it.name }
                ) { prayer ->
                    val isCurrent =
                        !state.isLoading &&
                                prayer.name == currentPrayerName

                    Surface(
                        modifier = Modifier
                            .width(cardWidth)
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                onClick = onOpenPrayers
                            ),
                        color = if (isCurrent) {
                            colors.hero
                        } else {
                            colors.surface
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 4.dp,
                                vertical = 14.dp
                            ),
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = prayer.name,
                                fontFamily = font,
                                fontSize = 11.sp,
                                color = if (isCurrent) {
                                    Color.White
                                } else {
                                    colors.text
                                },
                                maxLines = 1
                            )

                            CompositionLocalProvider(
                                LocalLayoutDirection provides
                                        LayoutDirection.Ltr
                            ) {
                                Text(
                                    text = prayer.time,
                                    fontSize = 15.sp,
                                    color = if (isCurrent) {
                                        Color.White
                                    } else {
                                        colors.accent
                                    },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        state.error?.let {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "تعذر تحميل المواقيت",
                    color = colors.secondary,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = viewModel::refresh
                ) {
                    Text(
                        text = "إعادة المحاولة",
                        color = colors.accent,
                        fontSize = 11.sp
                    )
                }
            }
        }

    }
}

@Composable
private fun HomeTopBar(
    isDark: Boolean,
    colors: HomeColors,
    font: FontFamily,
    onReaderClick: () -> Unit,
    onThemeClick: () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colors.surface,
                shape = RoundedCornerShape(14.dp)
            ) {
                IconButton(
                    onClick = onReaderClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (isDark) {
                                R.drawable.list_svg_icon_light
                            } else {
                                R.drawable.list_svg_icon_dark
                            }
                        ),
                        contentDescription = "اختيار القارئ",
                        tint = colors.text,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            Text(
                text = "توبة",
                color = colors.accent,
                fontFamily = font,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Surface(
                color = colors.surface,
                shape = RoundedCornerShape(14.dp)
            ) {
                IconButton(
                    onClick = onThemeClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (isDark) {
                                R.drawable.sun_svg_icon
                            } else {
                                R.drawable.moon_svg_icon
                            }
                        ),
                        contentDescription = if (isDark) {
                            "تفعيل الوضع النهاري"
                        } else {
                            "تفعيل الوضع الليلي"
                        },
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeReadingCard(
    hasSavedReading: Boolean,
    canResume: Boolean,
    surahName: String?,
    ayahNumber: Int?,
    colors: HomeColors,
    titleFont: FontFamily,
    quranFont: FontFamily,
    onContinue: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = colors.hero
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (hasSavedReading) {
                        "القراءة الأخيرة"
                    } else {
                        "ابدأ رحلتك مع القرآن"
                    },
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = titleFont,
                    fontSize = 12.sp
                )

                Text(
                    text = if (hasSavedReading) {
                        surahName.orEmpty()
                    } else {
                        "وردك اليومي"
                    },
                    color = Color.White,
                    fontFamily = quranFont,
                    fontSize = 28.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (
                        hasSavedReading &&
                        ayahNumber != null
                    ) {
                        "الآية $ayahNumber"
                    } else {
                        "ابدأ بسورة واختر وقتك المناسب"
                    },
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = colors.hero
                    ),
                    modifier = Modifier.heightIn(min = 48.dp),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text(
                        text = if (canResume) {
                            "متابعة القراءة"
                        } else {
                            "تصفح السور"
                        },
                        fontFamily = titleFont,
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.width(10.dp))

                    AnimatedContinueArrow(
                        color = colors.hero,
                        animate = canResume
                    )
                }
            }

            Image(
                painter = painterResource(
                    R.drawable.mosshaf
                ),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.65f)
                    .height(132.dp)
            )
        }
    }
}

@Composable
private fun AnimatedContinueArrow(
    color: Color,
    animate: Boolean
) {
    val offsetX = remember {
        Animatable(8f)
    }

    LaunchedEffect(animate) {
        if (!animate) {
            offsetX.snapTo(0f)
            return@LaunchedEffect
        }

        while (isActive) {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(500)
            )

            delay(600.milliseconds)

            offsetX.animateTo(
                targetValue = 8f,
                animationSpec = tween(500)
            )

            delay(600.milliseconds)
        }
    }

    Icon(
        painter = painterResource(
            R.drawable.arrow_left_icon
        ),
        contentDescription = null,
        tint = color,
        modifier = Modifier
            .size(18.dp)
            .offset {
                IntOffset(
                    x = offsetX.value.dp
                        .toPx()
                        .roundToInt(),
                    y = 0
                )
            }
    )
}

@Composable
private fun HomeSectionCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    backgroundColor: Color,
    textColor: Color,
    secondaryColor: Color,
    font: FontFamily,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp)
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = textColor,
                fontFamily = font,
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = subtitle,
                color = secondaryColor,
                fontSize = 11.sp,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))

            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.End)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeReaderSheet(
    selectedReaderId: String,
    colors: HomeColors,
    font: FontFamily,
    onDismiss: () -> Unit,
    onConfirm: (HomeReader) -> Unit
) {
    var query by rememberSaveable {
        mutableStateOf("")
    }

    var pendingReaderId by rememberSaveable(
        selectedReaderId
    ) {
        mutableStateOf(selectedReaderId)
    }

    val filteredReaders = remember(query) {
        val normalizedQuery = normalizeHomeSearch(query)

        if (normalizedQuery.isBlank()) {
            homeReaders
        } else {
            homeReaders.filter {
                normalizeHomeSearch(it.name)
                    .contains(normalizedQuery)
            }
        }
    }

    val pendingReader = remember(pendingReaderId) {
        homeReaders.firstOrNull {
            it.id == pendingReaderId
        } ?: homeReaders.first()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        containerColor = colors.surface
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "اختر صوت التلاوة",
                    color = colors.text,
                    fontFamily = font,
                    fontSize = 21.sp
                )

                Text(
                    text = "اختر القارئ ثم انتقل إلى قائمة السور.",
                    color = colors.secondary,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    placeholder = {
                        Text(
                            text = "ابحث عن قارئ",
                            fontSize = 13.sp
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.text,
                            unfocusedTextColor = colors.text,
                            focusedBorderColor = colors.accent,
                            unfocusedBorderColor =
                                colors.secondary.copy(alpha = 0.4f),
                            cursorColor = colors.accent,
                            focusedPlaceholderColor =
                                colors.secondary,
                            unfocusedPlaceholderColor =
                                colors.secondary
                        )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {
                    if (filteredReaders.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = "لا يوجد قارئ مطابق للبحث.",
                                color = colors.secondary,
                                modifier = Modifier.padding(
                                    vertical = 18.dp
                                )
                            )
                        }
                    }

                    items(
                        items = filteredReaders,
                        key = { it.id }
                    ) { reader ->
                        val selected =
                            reader.id == pendingReaderId

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selected) {
                                        colors.accent.copy(
                                            alpha = 0.12f
                                        )
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable {
                                    pendingReaderId = reader.id
                                }
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                                .heightIn(min = 48.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = null,
                                colors =
                                    RadioButtonDefaults.colors(
                                        selectedColor =
                                            colors.accent,
                                        unselectedColor =
                                            colors.secondary
                                    )
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = reader.name,
                                color = if (selected) {
                                    colors.accent
                                } else {
                                    colors.text
                                },
                                fontFamily = font,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        onConfirm(pendingReader)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.hero,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    )
                ) {
                    Text(
                        text = "استماع بصوت ${pendingReader.name}",
                        fontFamily = font,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}