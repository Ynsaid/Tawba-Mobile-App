package com.example.tawbaapp.screens

import com.example.tawbaapp.Classes.Surah
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.ViewModals.DarkModeViewModel
import com.example.tawbaapp.ViewModals.ReadingProgressViewModel
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Green

@Composable
fun SurahPage(
    navController: NavController,
    surah: Surah,
    darkModeViewModel: DarkModeViewModel,
    readingProgressViewModel: ReadingProgressViewModel
) {
    val isDark by darkModeViewModel.isDark.collectAsState()
    val lastRead by readingProgressViewModel.lastRead
    val listState = rememberLazyListState()

    val savedAyahIndex = remember(surah.id, lastRead) {
        if (lastRead?.surahId == surah.id) lastRead?.ayahIndex ?: 0 else 0
    }

    LaunchedEffect(savedAyahIndex) {
        val verses = surah.verses
        if (savedAyahIndex in verses.indices) {
            listState.scrollToItem(savedAyahIndex)
        }
    }

    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(firstVisibleIndex) {
        val verses = surah.verses
        if (firstVisibleIndex in verses.indices) {
            readingProgressViewModel.saveLastRead(surah, firstVisibleIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Black else Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(172.dp)
                .background(Green)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left_icon),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = surah.name,
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.amiri_regular)),
                    color = Color.White
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .width(180.dp),
                    thickness = 0.5.dp,
                    color = Color.LightGray
                )

                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.amiri_regular)),
                    color = Color.White
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            itemsIndexed(surah.verses) { _, ayah ->
                Text(
                    text = ayah.text,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.amiri_regular)),
                    lineHeight = 36.sp,
                    color = Green
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ayah.translation,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.cairo_regular)),
                    color = if (isDark) Color.White else Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}