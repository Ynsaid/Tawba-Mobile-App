package com.example.tawbaapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.classes.Surah
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Green
import com.example.tawbaapp.viewmodals.DarkModeViewModel
import com.example.tawbaapp.viewmodals.ReadingProgressViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.milliseconds

private val DarkBackground = Color(0xFF0E1410)
private val DarkSurface = Color(0xFF18211B)
private val DarkBorder = Color(0xFF2A362E)
private val DarkTextPrimary = Color(0xFFE6EEE8)
private val DarkTextSecondary = Color(0xFF9AA9A0)
private val DarkAccent = Color(0xFF5FCB8C)
private val DarkHeader = Color(0xFF1B6B43)

private val LightSurface = Color(0xFFF5F7F3)
private val LightBorder = Color(0xFFE3E8E1)
private val LightTextPrimary = Color(0xFF111111)
private val LightTextSecondary = Color(0xFF6B756C)

private val ArabicMarks = Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")

private fun normalizeArabic(text: String): String {
    return text
        .replace(ArabicMarks, "")
        .replace('أ', 'ا')
        .replace('إ', 'ا')
        .replace('آ', 'ا')
        .replace('ٱ', 'ا')
        .replace('ى', 'ي')
        .replace('ة', 'ه')
        .trim()
        .lowercase()
}

@OptIn(FlowPreview::class)
@Composable
fun SurahPage(
    navController: NavController,
    surah: Surah,
    darkModeViewModel: DarkModeViewModel,
    readingProgressViewModel: ReadingProgressViewModel
) {
    val isDark by darkModeViewModel.isDark.collectAsState()
    val lastRead by readingProgressViewModel.lastRead
    val focusManager = LocalFocusManager.current

    val pageBackground = if (isDark) DarkBackground else Color.White
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val borderColor = if (isDark) DarkBorder else LightBorder
    val textPrimary = if (isDark) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDark) DarkTextSecondary else LightTextSecondary
    val accentColor = if (isDark) DarkAccent else Green
    val headerColor = if (isDark) DarkHeader else Green

    val verses = surah.verses

    val initialIndex = remember(surah.id) {
        val saved = if (lastRead?.surahId == surah.id) {
            lastRead?.ayahIndex ?: 0
        } else {
            0
        }
        if (saved in verses.indices) saved else 0
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )

    var searchQuery by remember { mutableStateOf("") }

    val searchIndex = remember(surah.id) {
        verses.map { ayah ->
            normalizeArabic(ayah.text) + " " + ayah.translation.lowercase()
        }
    }

    val visibleIndices = remember(searchQuery, surah.id) {
        val query = normalizeArabic(searchQuery)

        if (query.isBlank()) {
            verses.indices.toList()
        } else {
            verses.indices.filter { searchIndex[it].contains(query) }
        }
    }

    val isSearching = searchQuery.isNotBlank()

    LaunchedEffect(surah.id) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .debounce(500.milliseconds)
            .distinctUntilChanged()
            .collect { index ->
                if (!isSearching && index in verses.indices) {
                    readingProgressViewModel.saveLastRead(surah, index)
                }
            }
    }

    LaunchedEffect(searchQuery) {
        if (isSearching) listState.scrollToItem(0)
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left_icon),
                    contentDescription = "رجوع",
                    tint = textPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(985f / 110f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.surah_header),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = surah.name,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily(Font(R.font.amiri_regular)),
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.34f)
                )
            }

            if (surah.id != 1 && surah.id != 9) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.amiri_regular)),
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                placeholder = {
                    Text(
                        text = "ابحث في آيات السورة...",
                        color = textSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "بحث",
                        tint = accentColor
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "مسح البحث",
                                tint = textSecondary
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = borderColor,
                    cursorColor = accentColor,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary
                )
            )

            if (visibleIndices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "لا توجد آيات مطابقة للبحث.",
                        color = textSecondary,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = visibleIndices,
                        key = { it }
                    ) { index ->
                        val ayah = verses[index]

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(surfaceColor)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        accentColor.copy(alpha = 0.14f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    fontSize = 13.sp,
                                    color = accentColor
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = ayah.text,
                                fontSize = 24.sp,
                                lineHeight = 40.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.amiri_regular)
                                ),
                                color = accentColor,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = ayah.translation,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.cairo_regular)
                                ),
                                color = textSecondary,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}