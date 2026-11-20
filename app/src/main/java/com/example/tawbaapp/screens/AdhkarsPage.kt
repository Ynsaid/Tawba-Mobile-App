package com.example.tawbaapp.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Green
import com.example.tawbaapp.viewmodals.DarkModeViewModel

private data class DhikrCategoryUi(
    val id: String,
    val title: String,
    val description: String,
    val imageRes: Int,
    val background: Color,
    val isDua: Boolean = false
)

private val dhikrCategories = listOf(
    DhikrCategoryUi(
        id = "morning",
        title = "أذكار الصباح",
        description = "أذكار بداية اليوم",
        imageRes = R.drawable.sun_svg_orange_icon_,
        background = Color(0xFFFFF2D8)
    ),
    DhikrCategoryUi(
        id = "evening",
        title = "أذكار المساء",
        description = "أذكار المساء",
        imageRes = R.drawable.moon_svg_icon,
        background = Color(0xFFE7EFFF)
    ),
    DhikrCategoryUi(
        id = "after_prayer",
        title = "بعد الصلاة",
        description = "أذكار عقب الصلاة",
        imageRes = R.drawable.pray_svg_icon,
        background = Color(0xFFE4F2E8)
    ),
    DhikrCategoryUi(
        id = "sleep",
        title = "قبل النوم",
        description = "أذكار النوم",
        imageRes = R.drawable.sleeping_svg_icon,
        background = Color(0xFFF6E7F2)
    ),
    DhikrCategoryUi(
        id = "wake_up",
        title = "الاستيقاظ",
        description = "أذكار الاستيقاظ",
        imageRes = R.drawable.getup_svg_icon,
        background = Color(0xFFEEE8FC)
    ),
    DhikrCategoryUi(
        id = "food",
        title = "الطعام",
        description = "أذكار الطعام",
        imageRes = R.drawable.eat_svg_icon,
        background = Color(0xFFFBE7E3)
    ),
    DhikrCategoryUi(
        id = "rizq",
        title = "دعاء الرزق",
        description = "الدعاء بالرزق",
        imageRes = R.drawable.rizq_svg_icon,
        background = Color(0xFFE7EFFF),
        isDua = true
    ),
    DhikrCategoryUi(
        id = "shifa",
        title = "دعاء الشفاء",
        description = "الدعاء للمريض",
        imageRes = R.drawable.shifa_svg_icon,
        background = Color(0xFFF6E7F2),
        isDua = true
    ),
    DhikrCategoryUi(
        id = "tawba",
        title = "دعاء التوبة",
        description = "التوبة والاستغفار",
        imageRes = R.drawable.tawba_svg_icon,
        background = Color(0xFFFFF2D8),
        isDua = true
    ),
    DhikrCategoryUi(
        id = "karb",
        title = "دعاء الكرب",
        description = "الدعاء عند الضيق",
        imageRes = R.drawable.karb_svg_icon,
        background = Color(0xFFE4F2E8),
        isDua = true
    ),
    DhikrCategoryUi(
        id = "rain",
        title = "دعاء المطر",
        description = "الدعاء عند المطر",
        imageRes = R.drawable.matar_svg_icon,
        background = Color(0xFFEEE8FC),
        isDua = true
    ),
    DhikrCategoryUi(
        id = "istikhara",
        title = "دعاء الاستخارة",
        description = "الاستخارة في الأمور",
        imageRes = R.drawable.istikhara_svg_icon,
        background = Color(0xFFFBE7E3),
        isDua = true
    )
)

private val dhikrSearchMarks =
    Regex("[\\u064B-\\u065F\\u0670\\u0640]")

private fun normalizeDhikrSearch(text: String): String {
    return text
        .replace(dhikrSearchMarks, "")
        .replace('أ', 'ا')
        .replace('إ', 'ا')
        .replace('آ', 'ا')
        .replace('ى', 'ي')
        .trim()
}

@Composable
fun AdhkarsPage(
    navController: NavController,
    darkModeViewModel: DarkModeViewModel
) {
    val isDark by darkModeViewModel.isDark.collectAsState()

    val amiri = remember {
        FontFamily(Font(R.font.amiri_regular))
    }

    var selectedTab by rememberSaveable {
        mutableStateOf(0)
    }

    var query by rememberSaveable {
        mutableStateOf("")
    }

    val background = if (isDark) {
        Color(0xFF0E1410)
    } else {
        Color(0xFFF6F8F5)
    }

    val surface = if (isDark) {
        Color(0xFF18221C)
    } else {
        Color.White
    }

    val textColor = if (isDark) {
        Color(0xFFE7EFE9)
    } else {
        Color(0xFF17251D)
    }

    val secondaryColor = if (isDark) {
        Color(0xFFA2B2A7)
    } else {
        Color(0xFF66766C)
    }

    val accent = if (isDark) {
        Color(0xFF76D6A0)
    } else {
        Green
    }

    val filteredItems = remember(selectedTab, query) {
        val search = normalizeDhikrSearch(query)

        dhikrCategories.filter { category ->
            val matchesTab =
                category.isDua == (selectedTab == 1)

            val matchesSearch = search.isBlank() ||
                    normalizeDhikrSearch(
                        category.title + " " + category.description
                    ).contains(search)

            matchesTab && matchesSearch
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            containerColor = background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // اتجاه الشريط ثابت لإبقاء سهم الرجوع يسارًا.
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Ltr
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    R.drawable.arrow_left_icon
                                ),
                                contentDescription = "رجوع",
                                tint = accent,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "الأذكار والأدعية",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = textColor,
                            fontFamily = amiri,
                            fontSize = 22.sp
                        )

                        Spacer(Modifier.size(48.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        ),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "أذكار وأدعية ليومك",
                        color = textColor,
                        fontFamily = amiri,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "اختر القسم المناسب، أو ابحث عن عنوانه.",
                        color = secondaryColor,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DhikrTabButton(
                        title = "الأذكار",
                        selected = selectedTab == 0,
                        accent = accent,
                        surface = surface,
                        textColor = textColor,
                        font = amiri,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedTab = 0
                        }
                    )

                    DhikrTabButton(
                        title = "الأدعية",
                        selected = selectedTab == 1,
                        accent = accent,
                        surface = surface,
                        textColor = textColor,
                        font = amiri,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedTab = 1
                        }
                    )
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                        color = textColor
                    ),
                    placeholder = {
                        Text(
                            text = "ابحث في الأقسام",
                            color = secondaryColor,
                            textAlign = TextAlign.Start
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    query = ""
                                }
                            ) {
                                Text(
                                    text = "مسح",
                                    color = accent
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = surface,
                        unfocusedContainerColor = surface,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accent,
                        unfocusedBorderColor =
                            secondaryColor.copy(alpha = 0.3f),
                        cursorColor = accent
                    )
                )

                Text(
                    text = "عدد الأقسام: ${filteredItems.size}",
                    color = secondaryColor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 6.dp
                        )
                )

                if (filteredItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "لا توجد نتائج",
                            color = textColor,
                            fontFamily = amiri,
                            fontSize = 24.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "جرّب كلمة أخرى أو غيّر التبويب.",
                            color = secondaryColor,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(
                            minSize = 145.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 24.dp
                        ),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredItems,
                            key = { it.id }
                        ) { category ->
                            DhikrCategoryCard(
                                category = category,
                                isDark = isDark,
                                surface = surface,
                                textColor = textColor,
                                secondaryColor = secondaryColor,
                                font = amiri,
                                onClick = {
                                    // نحافظ على استخدام العنوان
                                    // كما في شاشة التفاصيل الأصلية.
                                    val title = Uri.encode(category.title)

                                    navController.navigate(
                                        "adhkar_detail/$title"
                                    ) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DhikrTabButton(
    title: String,
    selected: Boolean,
    accent: Color,
    surface: Color,
    textColor: Color,
    font: FontFamily,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            // تحديد الارتفاع يمنع التمدد الذي ظهر بالصورة.
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) {
            accent.copy(alpha = 0.14f)
        } else {
            surface
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = if (selected) accent else textColor,
                fontFamily = font,
                fontSize = 19.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DhikrCategoryCard(
    category: DhikrCategoryUi,
    isDark: Boolean,
    surface: Color,
    textColor: Color,
    secondaryColor: Color,
    font: FontFamily,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isDark) {
            surface
        } else {
            category.background
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isDark) {
                            category.background.copy(alpha = 0.15f)
                        } else {
                            Color.White.copy(alpha = 0.7f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(category.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = category.title,
                color = textColor,
                fontFamily = font,
                fontSize = 21.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = category.description,
                color = secondaryColor,
                fontSize = 12.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}