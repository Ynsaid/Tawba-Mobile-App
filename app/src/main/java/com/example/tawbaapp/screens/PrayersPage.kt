package com.example.tawbaapp.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.ViewModals.DarkModeViewModel
import com.example.tawbaapp.ViewModals.PrayerViewModel
import com.example.tawbaapp.R
import com.example.tawbaapp.Classes.LocationDataStore
import com.example.tawbaapp.ui.theme.Bg_Prayer
import com.example.tawbaapp.ui.theme.Black
import com.example.tawbaapp.ui.theme.Green
import com.example.tawbaapp.ui.theme.Grey
import com.example.tawbaapp.ui.theme.To_white
import com.example.tawbaapp.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrayersPage(
    navController: NavController,
    viewModel: PrayerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    darkModeViewModel: DarkModeViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val cairoBold = FontFamily(Font(R.font.cairo_blod))
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val visible = remember { mutableStateOf(true) }
    val targetAlpha = if (visible.value) 1f else 0f
    val animatedAlpha by animateFloatAsState(targetValue = targetAlpha, label = "")
    var selectedCity by remember { mutableStateOf("مدينة") }
    var selectedWilaya by remember { mutableStateOf("الولاية") }

    var citySelector  by remember { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true )
    // set dark mode
    val isDark by darkModeViewModel.isDark.collectAsState()
    val context = LocalContext.current
    val locationStore = remember { LocationDataStore(context) }

    LaunchedEffect(Unit) {
        val (savedCity, savedWilaya) = locationStore.loadLocation()
        selectedCity = savedCity
        selectedWilaya = savedWilaya
        viewModel.fetchPrayerTimes(savedCity, savedWilaya)
    }


    val now = remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            visible.value = !visible.value
            now.value = LocalTime.now()
        }
    }

    // Format and order prayers
    val prayers = listOf(
        "الفجر" to prayerTimes?.fajr,
        "الظهر" to prayerTimes?.dhuhr,
        "العصر" to prayerTimes?.asr,
        "المغرب" to prayerTimes?.maghrib,
        "العشاء" to prayerTimes?.isha
    ).mapNotNull { (name, time) ->
        time?.let {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val localTime = try { LocalTime.parse(it, formatter) } catch (e: Exception) { null }
            localTime?.let { lt -> Triple(name, it, lt) }
        }
    }.sortedBy { it.third }

    val currentPrayer = prayers.lastOrNull { it.third <= now.value }

    val nowDateTime = LocalDateTime.of(LocalDate.now(), now.value)

    val nextPrayer = prayers.firstOrNull {
        val prayerDateTime = LocalDateTime.of(LocalDate.now(), it.third)
        prayerDateTime.isAfter(nowDateTime)
    } ?: run {
        val fajr = prayers.firstOrNull { it.first == "الفجر" }
        fajr?.let {
            val nextFajrTime = LocalDateTime.of(LocalDate.now().plusDays(1), it.third)
            Triple(it.first, it.second, nextFajrTime.toLocalTime())
        }
    }


    val countdown = nextPrayer?.third?.let {
        val nextTime = if (it.isBefore(now.value)) it.plusHours(24) else it
        val duration = Duration.between(now.value, nextTime)
        String.format(
            "%02d:%02d:%02d",
            duration.toHours(),
            duration.toMinutes() % 60,
            duration.seconds % 60
        )
    } ?: "--:--:--"


    Box(modifier = Modifier.fillMaxSize()
        .background(if(isDark) Color.Black else Color.White)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Green)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top =32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left_icon),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(To_white)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedCity -$selectedWilaya",
                        fontSize = 14.sp,
                        fontFamily = cairoBold,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { citySelector = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.location_icon),
                            contentDescription = "Location",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mosque_svg_icon),
                        contentDescription = "Mosque Icon"
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "الصلاة الحالية",
                        fontSize = 14.sp,
                        fontFamily = cairoBold,
                        color = Grey,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Text(
                        text = "${currentPrayer?.first ?: "--"} | ${currentPrayer?.second ?: "--:--"}",
                        fontSize = 14.sp,
                        fontFamily = cairoBold,
                        fontWeight = FontWeight.Bold,
                        color = Grey,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الصلاة القادمة بعد",
                        fontSize = 20.sp,
                        fontFamily = cairoBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(30.dp)
                            .align(Alignment.End),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = countdown,
                            fontSize = 20.sp,
                            fontFamily = cairoBold,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.alpha(animatedAlpha)
                        )
                    }
                }
            }
        }

        // Body
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            prayers.forEach { (name, time, _) ->
                PrayerBox(
                    name = name,
                    time = time,
                    font = cairoBold,
                    isCurrent = name == currentPrayer?.first
                )
            }
        }
        CityPickerBottomSheet(
            citySelector = citySelector,
            sheetState = sheetState,
            bg = isDark,
            onDismiss = { citySelector = false },
            onCitySelected = { wilaya, city ->
                selectedCity = city
                selectedWilaya = wilaya
                viewModel.fetchPrayerTimes(city, wilaya)

                coroutineScope.launch {
                    locationStore.saveLocation(city, wilaya)
                }

                citySelector = false
            }
        )


    }
}

@Composable
fun PrayerBox(name: String, time: String, font: FontFamily, isCurrent: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrent) Green else Bg_Prayer)
            .height(50.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = time,
            fontSize = 20.sp,
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            color = if (isCurrent) Color.White else Green
        )
        Text(
            text = name,
            fontSize = 20.sp,
            fontFamily = font,
            color = if (isCurrent) Color.White else Color.Black
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerBottomSheet(
    citySelector: Boolean,
    sheetState: SheetState,
    bg : Boolean,
    onDismiss: () -> Unit,
    onCitySelected: (selectedWilaya: String, selectedCity: String) -> Unit
) {
    val wilayas = listOf("Medea", "Algiers", "Oran")
    val citiesByWilaya = mapOf(
        "Medea" to listOf("El Omaria", "Ksar El Boukhari", "Berrouaghia"),
        "Algiers" to listOf("Bab El Oued", "El Harrach", "Hydra"),
        "Oran" to listOf("Bir El Djir", "Es Senia", "Oran")
    )

    var selectedWilaya by remember { mutableStateOf(wilayas.first()) }
    var selectedCity by remember { mutableStateOf(citiesByWilaya[selectedWilaya]?.first() ?: "") }

    if (citySelector) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState,
            containerColor = if (bg) Black else White,
            tonalElevation = 4.dp,
            dragHandle = {
                Box(
                    Modifier
                        .padding(vertical = 8.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray, shape = MaterialTheme.shapes.small)
                )
            },
            modifier = Modifier.fillMaxHeight(0.5f)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "اختر الولاية والمدينة",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.cairo_blod)),
                        color = Green,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Wilaya Dropdown
                    var wilayaExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = wilayaExpanded,
                        onExpandedChange = { wilayaExpanded = !wilayaExpanded },
                        modifier = Modifier.fillMaxWidth()
                            .background(White)
                    ) {
                        TextField(
                            value = selectedWilaya,
                            onValueChange = {},
                            readOnly = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Green,
                                unfocusedIndicatorColor = Green,
                                cursorColor = Green,

                            ),
                            textStyle = LocalTextStyle.current.copy(
                                fontFamily = FontFamily(Font(R.font.cairo_blod)),
                                fontSize = 16.sp,
                                textAlign = TextAlign.End,
                                color = if (bg) Color.White else Color.Black
                            ),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = wilayaExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .border(1.dp, Green, shape = RoundedCornerShape(8.dp))
                        )
                        ExposedDropdownMenu(
                            expanded = wilayaExpanded,
                            onDismissRequest = { wilayaExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                                .background(White)
                                .align(Alignment.End)
                        ) {
                            wilayas.forEach { wilaya ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            wilaya,
                                            fontFamily = FontFamily(Font(R.font.cairo_blod)),
                                            color = Green,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    onClick = {
                                        selectedWilaya = wilaya
                                        selectedCity = citiesByWilaya[wilaya]?.first() ?: ""
                                        wilayaExpanded = false
                                    },

                                )
                            }
                        }
                    }

                    // City Dropdown
                    var cityExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = { cityExpanded = !cityExpanded },
                        modifier = Modifier.fillMaxWidth()
                            .background(White)
                            .align(Alignment.End)
                    ) {
                        TextField(
                            value = selectedCity,
                            onValueChange = {},
                            readOnly = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Green,
                                unfocusedIndicatorColor = Green,
                                cursorColor = Green,
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                fontFamily = FontFamily(Font(R.font.cairo_blod)),
                                fontSize = 16.sp,
                                textAlign = TextAlign.End,
                                color = if (bg) Color.White else Color.Black

                            ),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .border(1.dp, Green, shape = RoundedCornerShape(8.dp))
                        )
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            citiesByWilaya[selectedWilaya]?.forEach { city ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            city,
                                            fontFamily = FontFamily(Font(R.font.cairo_blod)),
                                            color = Green,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    onClick = {
                                        selectedCity = city
                                        cityExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Validate Button
                    Button(
                        onClick = {
                            onCitySelected(selectedWilaya, selectedCity)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "تأكيد الاختيار",
                            fontFamily = FontFamily(Font(R.font.cairo_blod)),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
