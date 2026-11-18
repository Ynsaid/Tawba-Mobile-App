package com.example.tawbaapp.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.google.gson.Gson
import java.net.URLEncoder

private val DarkBackground = Color(0xFF0E1410)
private val DarkSurface = Color(0xFF18211B)

private val DarkBorder = Color(0xFF2A362E)
private val DarkTextPrimary = Color(0xFFE6EEE8)
private val DarkTextSecondary = Color(0xFF9AA9A0)
private val DarkAccent = Color(0xFF5FCB8C)

private val LightBackground = Color.White
private val LightSurface = Color(0xFFF5F7F3)
private val LightBorder = Color(0xFFE3E8E1)
private val LightTextPrimary = Color(0xFF111111)
private val LightTextSecondary = Color(0xFF6B756C)

private fun normalizeArabic(text: String): String {
    return text
        .replace(Regex("[\\u064B-\\u065F\\u0670\\u0640]"), "")
        .replace('أ', 'ا')
        .replace('إ', 'ا')
        .replace('آ', 'ا')
        .replace('ى', 'ي')
        .replace('ة', 'ه')
        .replace("-", "")
        .replace("'", "")
        .replace("’", "")
        .trim()
        .lowercase()
}

@Composable
fun ReadingPage(
    navController: NavController,
    surahs: List<Surah>,
    darkModeViewModel: DarkModeViewModel,

) {
    val isDark by darkModeViewModel.isDark.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val backgroundColor by animateColorAsState(
        targetValue = if (isDark) DarkBackground else LightBackground,
        animationSpec = tween(600),
        label = "BackgroundTransition"
    )

    val surfaceColor by animateColorAsState(
        targetValue = if (isDark) DarkSurface else LightSurface,
        animationSpec = tween(600),
        label = "SurfaceTransition"
    )

    val borderColor = if (isDark) DarkBorder else LightBorder
    val textPrimary = if (isDark) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDark) DarkTextSecondary else LightTextSecondary
    val accentColor = if (isDark) DarkAccent else Green

    val iconRes = if (isDark) {
        R.drawable.sun_svg_icon
    } else {
        R.drawable.moon_svg_icon
    }

    val filteredSurahs = remember(searchQuery, surahs) {
        val query = normalizeArabic(searchQuery)

        if (query.isBlank()) {
            surahs
        } else {
            surahs.filter { surah ->
                normalizeArabic(surah.name).contains(query) ||
                        normalizeArabic(surah.transliteration).contains(query) ||
                        surah.id.toString().contains(query)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 32.dp, 16.dp, 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.arrow_left_icon
                    ),
                    contentDescription = "Return Back",
                    colorFilter = ColorFilter.tint(textPrimary),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.popBackStack() }
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "القران",
                    fontFamily = FontFamily(Font(R.font.amiri_bold)),
                    fontSize = 32.sp,
                    color = accentColor
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { darkModeViewModel.toggleTheme() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Toggle Theme",
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                            color = textSecondary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
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
                            IconButton(
                                onClick = { searchQuery = "" }
                            ) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredSurahs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "لا توجد نتائج مطابقة للبحث.",
                        color = textSecondary,
                        fontFamily = FontFamily(
                            Font(R.font.amiri_regular)
                        ),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = filteredSurahs,
                        key = { it.id }
                    ) { surah ->
                        SurahItem(
                            surah = surah,
                            surfaceColor = surfaceColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor,
                            onItemClick = {
                                val json = URLEncoder.encode(
                                    Gson().toJson(surah),
                                    "UTF-8"
                                )
                                navController.navigate("surah/$json")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurahItem(
    surah: Surah,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    onItemClick: (Surah) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .clickable { onItemClick(surah) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = surah.transliteration,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.cairo_blod)),
            modifier = Modifier.weight(1f),
            color = textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = surah.name,
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.amiri_bold)),
                color = accentColor
            )

            Text(
                text = if (surah.type.lowercase() == "meccan") {
                    "مكية"
                } else {
                    "مدنية"
                },
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.amiri_regular)),
                color = textSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = surah.id.toString(),
                fontSize = if (surah.id > 99) 14.sp else 16.sp,
                fontFamily = FontFamily(Font(R.font.amiri_regular)),
                color = accentColor
            )
        }
    }
}