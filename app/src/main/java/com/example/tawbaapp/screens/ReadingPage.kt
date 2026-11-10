package com.example.tawbaapp.screens

import com.example.tawbaapp.Classes.Surah
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.tawbaapp.ViewModals.DarkModeViewModel
import com.example.tawbaapp.R
import com.example.tawbaapp.ViewModals.ReadingProgressViewModel
import com.example.tawbaapp.ui.theme.Green
import com.google.gson.Gson
import java.net.URLEncoder

@Composable
fun ReadingPage(
    navController: NavController,
    surahs: List<Surah>,
    darkModeViewModel: DarkModeViewModel,
    readingProgressViewModel: ReadingProgressViewModel
) {
    val isDark by darkModeViewModel.isDark.collectAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF101010) else Color.White,
        animationSpec = tween(1000),
        label = "BackgroundTransition"
    )

    val textColor = if (isDark) Color.White else Color.Black
    val iconRes = if (isDark) R.drawable.sun_svg_icon else R.drawable.moon_svg_icon

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
                    painter = painterResource(id = R.drawable.arrow_left_icon),
                    contentDescription = "Return Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.popBackStack() }
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "القران",
                    fontFamily = FontFamily(Font(R.font.amiri_bold)),
                    fontSize = 32.sp,
                    color = Green
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

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(surahs) { surah ->
                    SurahItem(
                        surah = surah,
                        textColor = textColor,
                        onItemClick = {
                            val json = URLEncoder.encode(Gson().toJson(surah), "UTF-8")
                            navController.navigate("surah/$json")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SurahItem(
    surah: Surah,
    textColor: Color,
    onItemClick: (Surah) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onItemClick(surah) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = surah.transliteration,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.cairo_blod)),
            modifier = Modifier.weight(1f),
            color = textColor
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = surah.name,
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.amiri_bold)),
                color = Green
            )
            Text(
                text = if (surah.type.lowercase() == "meccan") "مكية" else "مدنية",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.amiri_regular)),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = surah.id.toString(),
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.amiri_regular)),
            color = Color.Gray
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        thickness = 0.5.dp,
        color = Color(0xFFCCCCCC)
    )
}