package com.example.tawbaapp.Screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.ViewModals.DarkModeViewModel
import com.example.tawbaapp.R
import com.example.tawbaapp.Classes.ReaderPreference
import com.example.tawbaapp.Classes.Surah
import com.example.tawbaapp.ViewModals.ReadingProgressViewModel
import com.example.tawbaapp.ui.theme.Green
import com.example.tawbaapp.ui.theme.To_Yellow
import com.example.tawbaapp.ui.theme.To_blue
import com.example.tawbaapp.ui.theme.To_green
import com.example.tawbaapp.ui.theme.To_pink
import com.example.tawbaapp.ui.theme.White
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionPage(
    navController: NavController,
    darkModeViewModel: DarkModeViewModel,
    readingProgressViewModel: ReadingProgressViewModel,
    surahList: List<Surah>
) {
    val isDark by darkModeViewModel.isDark.collectAsState()
    val offsetX = remember { Animatable(8f) }
    var showSheet by remember { mutableStateOf(false) }
    val setList = if (isDark) R.drawable.list_svg_icon_light else R.drawable.list_svg_icon_dark
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lastRead by readingProgressViewModel.lastRead
    val scope = rememberCoroutineScope()

    val listOfReader = listOf(
        "العفاسي" to "afs",
        "أيوب بن محمد بن أحمد" to "ayyub",
        "أحمد الحذيفي" to "ahmad_huth",
        "إبراهيم الأخضر" to "bna",
        "البرمي" to "brmi",
        "بندر بليلة" to "bu_khtr",
        "أبوعبدالله جابر" to "buajan",
        "الدليمي" to "dlami",
        "فهد العتيبي" to "fahad_otibi",
        "فواز السعيد" to "fawaz",
        "فارس عباد" to "frs_a"
    )

    var readerExpanded by remember { mutableStateOf(false) }
    var selectedReader by remember { mutableStateOf(listOfReader.first().first) }
    var selectedReaderId by remember { mutableStateOf(listOfReader.first().second) }

    val context = LocalContext.current
    val readerPreference = remember { ReaderPreference(context) }

    LaunchedEffect(Unit) {
        while (true) {
            offsetX.animateTo(0f, animationSpec = tween(500))
            delay(600)
            offsetX.animateTo(8f, animationSpec = tween(500))
            delay(600)
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF101010) else Color.White,
        animationSpec = tween(durationMillis = 1000),
        label = "BackgroundColorTransition"
    )

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
                    painter = painterResource(id = setList),
                    contentDescription = "Sidebar",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { showSheet = true }
                )

                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false },
                        sheetState = sheetState,
                        containerColor = if (isDark) Color.Black else Color.White,
                        tonalElevation = 4.dp,
                        dragHandle = {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(Color.Gray, shape = MaterialTheme.shapes.small)
                            )
                        },
                        modifier = Modifier.fillMaxHeight(0.4f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = readerExpanded,
                                    onExpandedChange = { readerExpanded = !readerExpanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextField(
                                        value = selectedReader,
                                        onValueChange = {},
                                        readOnly = true,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Green,
                                            unfocusedIndicatorColor = Green,
                                            cursorColor = Green
                                        ),
                                        textStyle = LocalTextStyle.current.copy(
                                            fontFamily = FontFamily(Font(R.font.cairo_blod)),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.End,
                                            color = if (isDark) Color.White else Color.Black
                                        ),
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = readerExpanded)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                            .border(1.dp, Green, shape = RoundedCornerShape(8.dp))
                                    )

                                    ExposedDropdownMenu(
                                        expanded = readerExpanded,
                                        onDismissRequest = { readerExpanded = false }
                                    ) {
                                        listOfReader.forEach { (name, id) ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = name,
                                                        fontFamily = FontFamily(Font(R.font.cairo_blod)),
                                                        color = Green,
                                                        textAlign = TextAlign.End,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                },
                                                onClick = {
                                                    selectedReader = name
                                                    selectedReaderId = id
                                                    readerExpanded = false
                                                },
                                                modifier = Modifier.background(if (isDark) Color.Black else Color.White)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            readerPreference.saveReaderId(selectedReaderId)
                                            navController.navigate("listing/$selectedReaderId")
                                            showSheet = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Green,
                                        contentColor = White
                                    )
                                ) {
                                    Text(
                                        text = "استماع بصوت $selectedReader",
                                        fontFamily = FontFamily(Font(R.font.cairo_blod)),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "توبة",
                    fontFamily = FontFamily(Font(R.font.cairo_blod)),
                    fontSize = 32.sp,
                    color = if (isDark) Color.White else Green
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { darkModeViewModel.toggleTheme() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Dark mode toggle",
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .width(358.dp)
                    .height(172.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Green)
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mosshaf),
                    contentDescription = "Quran",
                    modifier = Modifier
                        .size(190.dp, 156.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "القراءة الأخيرة",
                            fontFamily = FontFamily(Font(R.font.cairo_blod)),
                            fontSize = 12.sp,
                            color = White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lastRead?.surahName ?: "--",
                            fontFamily = FontFamily(Font(R.font.amiri_regular)),
                            fontSize = 20.sp,
                            color = White
                        )

                        Text(
                            text = "آية ${lastRead?.ayahIndex?.plus(1) ?: "--"}",
                            fontFamily = FontFamily(Font(R.font.amiri_regular)),
                            fontSize = 14.sp,
                            color = White
                        )
                    }

                    ContinueButton(
                        offsetX = offsetX,
                        navController = navController,
                        readingProgressViewModel = readingProgressViewModel,
                        surahList = surahList
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "الاقسام",
                fontFamily = FontFamily(Font(R.font.cairo_blod)),
                fontSize = 24.sp,
                color =   if (isDark) Color.White else Green,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionCard(
                        title = "القراءة",
                        imageRes = R.drawable.quran_svg_iconv2,
                        height = 134.dp,
                        backgroundColor = To_blue
                    ) {
                        navController.navigate("reading")
                    }

                    SectionCard(
                        title = "مواقيت الصلاة",
                        imageRes = R.drawable.pray_svg_icon,
                        height = 198.dp,
                        backgroundColor = To_green
                    ) {
                        navController.navigate("prayers")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionCard(
                        title = "الاستماع",
                        imageRes = R.drawable.listeninig_svg_icon,
                        height = 198.dp,
                        backgroundColor = To_pink
                    ) {
                        navController.navigate("listing/afs")
                    }

                    SectionCard(
                        title = "أذكار",
                        imageRes = R.drawable.sebha_svg_icon,
                        height = 134.dp,
                        backgroundColor = To_Yellow
                    ) {
                        navController.navigate("adhkars")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    imageRes: Int,
    height: Dp,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            fontFamily = FontFamily(Font(R.font.cairo_blod)),
            fontSize = 16.sp,
            color = Green,
            textAlign = TextAlign.End,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(98.dp)
                .align(Alignment.BottomStart)
        )
    }
}

@Composable
fun ContinueButton(
    offsetX: Animatable<Float, AnimationVector1D>,
    navController: NavController,
    readingProgressViewModel: ReadingProgressViewModel,
    surahList: List<Surah>
) {
    val lastRead by readingProgressViewModel.lastRead

    Button(
        onClick = {
            val saved = lastRead
            if (saved != null) {
                val surah = surahList.find { it.id == saved.surahId }
                if (surah != null) {
                    val json = URLEncoder.encode(Gson().toJson(surah), "UTF-8")
                    navController.navigate("surah/$json")
                }
            }
        },
        enabled = lastRead != null,
        modifier = Modifier
            .height(40.dp)
            .width(140.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = Green,
            disabledContainerColor = White,
            disabledContentColor = Green
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left_icon),
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 4.dp)
                    .offset(x = offsetX.value.dp),
                tint = Green
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "أكمل",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.cairo_blod))
            )
        }
    }
}