package com.example.tawbaapp.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import androidx.navigation.NavController
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Green
import com.example.tawbaapp.viewmodals.DarkModeViewModel
import com.example.tawbaapp.viewmodals.PrayerDayData
import com.example.tawbaapp.viewmodals.PrayerViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private data class SalahColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val secondary: Color,
    val accent: Color,
    val header: Color
)

private data class SalahItem(
    val name: String,
    val time: String
)

private class PrayerLocationDisabled : Exception()

private fun hasPrayerLocationPermission(
    context: Context
): Boolean {
    return listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).any {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@SuppressLint("MissingPermission")
private suspend fun getPrayerDeviceLocation(
    context: Context
): Location {
    if (!hasPrayerLocationPermission(context)) {
        throw SecurityException()
    }

    val manager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager

    if (!LocationManagerCompat.isLocationEnabled(manager)) {
        throw PrayerLocationDisabled()
    }

    val fineGranted =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    val available = manager.getProviders(true)

    // قراءة واحدة فقط، وليس تتبعًا مستمرًا.
    val provider = when {
        fineGranted &&
                LocationManager.GPS_PROVIDER in available -> {
            LocationManager.GPS_PROVIDER
        }

        LocationManager.NETWORK_PROVIDER in available -> {
            LocationManager.NETWORK_PROVIDER
        }

        else -> throw IllegalStateException(
            "No location provider"
        )
    }

    return suspendCancellableCoroutine { continuation ->
        val cancellation = CancellationSignal()

        continuation.invokeOnCancellation {
            cancellation.cancel()
        }

        try {
            LocationManagerCompat.getCurrentLocation(
                manager,
                provider,
                cancellation,
                ContextCompat.getMainExecutor(context)
            ) { location ->
                if (continuation.isActive) {
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        continuation.resumeWithException(
                            IllegalStateException(
                                "Location unavailable"
                            )
                        )
                    }
                }
            }
        } catch (exception: Exception) {
            if (continuation.isActive) {
                continuation.resumeWithException(exception)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrayersPage(
    navController: NavController,
    viewModel: PrayerViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(),
    darkModeViewModel: DarkModeViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val isDark by darkModeViewModel.isDark.collectAsState()

    val colors = remember(isDark) {
        if (isDark) {
            SalahColors(
                background = Color(0xFF0E1410),
                surface = Color(0xFF18221C),
                text = Color(0xFFE7EFE9),
                secondary = Color(0xFFA2B2A7),
                accent = Color(0xFF76D6A0),
                header = Color(0xFF185D3D)
            )
        } else {
            SalahColors(
                background = Color(0xFFF6F8F5),
                surface = Color.White,
                text = Color(0xFF17251D),
                secondary = Color(0xFF66766C),
                accent = Green,
                header = Green
            )
        }
    }

    val font = remember {
        FontFamily(Font(R.font.cairo_blod))
    }

    var showLocationSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val prayers = remember(state.today) {
        state.today?.times?.let {
            listOf(
                SalahItem("الفجر", it.fajr),
                SalahItem("الظهر", it.dhuhr),
                SalahItem("العصر", it.asr),
                SalahItem("المغرب", it.maghrib),
                SalahItem("العشاء", it.isha)
            )
        }.orEmpty()
    }

    LaunchedEffect(viewModel) {
        while (isActive) {
            viewModel.refreshIfDateChanged()
            delay(30_000)
        }
    }

    Scaffold(
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.arrow_left_icon
                            ),
                            contentDescription = "رجوع",
                            tint = colors.text,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "مواقيت الصلاة",
                        color = colors.text,
                        fontFamily = font,
                        fontSize = 21.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    )
                }
            }

            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Rtl
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    item(key = "location") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showLocationSheet = true
                                },
                            color = colors.surface,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.location_icon
                                    ),
                                    contentDescription = null,
                                    tint = colors.accent,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = state.locationLabel,
                                        color = colors.text,
                                        fontFamily = font,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        text = if (state.isGpsLocation) {
                                            "مواقيت حسب الإحداثيات المحفوظة"
                                        } else {
                                            "مواقيت حسب المدينة"
                                        },
                                        color = colors.secondary,
                                        fontSize = 12.sp
                                    )
                                }

                                Text(
                                    text = "تغيير",
                                    color = colors.accent,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    if (state.isLoading) {
                        item(key = "loading") {
                            Surface(
                                color = colors.surface,
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(24.dp),
                                    verticalAlignment =
                                        Alignment.CenterVertically,
                                    horizontalArrangement =
                                        Arrangement.spacedBy(14.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = colors.accent,
                                        strokeWidth = 2.dp
                                    )

                                    Text(
                                        text = "جارٍ جلب المواقيت...",
                                        color = colors.text,
                                        fontFamily = font
                                    )
                                }
                            }
                        }
                    }

                    state.error?.let { error ->
                        item(key = "error") {
                            Surface(
                                color = colors.surface,
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = error,
                                        color = colors.text,
                                        lineHeight = 24.sp
                                    )

                                    TextButton(
                                        onClick = viewModel::refresh
                                    ) {
                                        Text(
                                            "إعادة المحاولة",
                                            color = colors.accent
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val today = state.today
                    val tomorrow = state.tomorrow

                    if (today != null && tomorrow != null) {
                        item(key = "countdown") {
                            PrayerCountdownCard(
                                today = today,
                                tomorrow = tomorrow,
                                prayers = prayers,
                                colors = colors,
                                font = font
                            )
                        }

                        item(key = "heading") {
                            Text(
                                text = "مواقيت اليوم",
                                color = colors.text,
                                fontFamily = font,
                                fontSize = 19.sp
                            )
                        }

                        items(
                            items = prayers,
                            key = { it.name }
                        ) { prayer ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = colors.surface,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 18.dp,
                                        vertical = 16.dp
                                    ),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = prayer.name,
                                        fontFamily = font,
                                        color = colors.text,
                                        fontSize = 18.sp
                                    )

                                    Spacer(Modifier.weight(1f))

                                    PrayerClockText(
                                        value = prayer.time,
                                        color = colors.accent,
                                        size = 22
                                    )
                                }
                            }
                        }

                        item(key = "sunrise") {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = colors.surface,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(18.dp),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الشروق",
                                        color = colors.secondary,
                                        fontFamily = font
                                    )

                                    Spacer(Modifier.weight(1f))

                                    PrayerClockText(
                                        value = today.sunrise,
                                        color = colors.secondary,
                                        size = 18
                                    )
                                }
                            }
                        }

                        item(key = "metadata") {
                            Surface(
                                color = colors.surface,
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "معلومات المواقيت",
                                        color = colors.text,
                                        fontFamily = font
                                    )

                                    Text(
                                        text = "طريقة الحساب: ${today.methodName}",
                                        color = colors.secondary,
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = "المنطقة الزمنية: ${today.zoneId}",
                                        color = colors.secondary,
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = "قد تختلف المواقيت عن التوقيت المعتمد في مسجدك؛ راجع التوقيت المحلي عند الحاجة.",
                                        color = colors.secondary,
                                        fontSize = 12.sp,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }

                        item(key = "refresh") {
                            OutlinedButton(
                                onClick = viewModel::refresh,
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = "تحديث المواقيت",
                                    color = colors.accent,
                                    fontFamily = font
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLocationSheet) {
        PrayerPlacePicker(
            colors = colors,
            font = font,
            onDismiss = {
                showLocationSheet = false
            },
            onCitySelected = { city ->
                showLocationSheet = false

                viewModel.fetchPrayerTimes(
                    city = city,
                    country = "DZ"
                )
            },
            onDefaultSelected = {
                showLocationSheet = false
                viewModel.useDefaultLocation()
            },
            onCoordinatesSelected = { latitude, longitude ->
                showLocationSheet = false

                viewModel.fetchPrayerTimesByCoordinates(
                    latitude = latitude,
                    longitude = longitude
                )
            }
        )
    }
}

@Composable
private fun PrayerClockText(
    value: String,
    color: Color,
    size: Int
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr
    ) {
        Text(
            text = value,
            color = color,
            fontSize = size.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PrayerCountdownCard(
    today: PrayerDayData,
    tomorrow: PrayerDayData,
    prayers: List<SalahItem>,
    colors: SalahColors,
    font: FontFamily
) {
    val zone = remember(today.zoneId) {
        ZoneId.of(today.zoneId)
    }

    var now by remember(zone) {
        mutableStateOf(ZonedDateTime.now(zone))
    }

    LaunchedEffect(zone) {
        while (isActive) {
            now = ZonedDateTime.now(zone)
            delay(1000)
        }
    }

    // قائمة المواعيد تنشأ عند تغير البيانات فقط.
    val schedule = remember(today, tomorrow, prayers) {
        val currentDay = prayers.map {
            it.name to today.date
                .atTime(LocalTime.parse(it.time))
                .atZone(zone)
        }

        currentDay + (
                "الفجر" to tomorrow.date
                    .atTime(LocalTime.parse(tomorrow.times.fajr))
                    .atZone(zone)
                )
    }

    val next = schedule.firstOrNull {
        it.second.isAfter(now)
    }

    val remaining = next?.let {
        Duration.between(now, it.second)
            .seconds
            .coerceAtLeast(0)
    }

    val countdown = remaining?.let {
        String.format(
            Locale.US,
            "%02d:%02d:%02d",
            it / 3600,
            it % 3600 / 60,
            it % 60
        )
    } ?: "--:--:--"

    val dateText = today.date.format(
        DateTimeFormatter.ofPattern(
            "EEEE، d MMMM yyyy",
            Locale.forLanguageTag("ar-DZ")
        )
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.header,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = dateText,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )

            Text(
                text = "الصلاة القادمة",
                color = Color.White.copy(alpha = 0.85f),
                fontFamily = font,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = next?.first ?: "—",
                    color = Color.White,
                    fontFamily = font,
                    fontSize = 30.sp,
                    modifier = Modifier.weight(1f)
                )

                PrayerClockText(
                    value = next?.second?.format(
                        DateTimeFormatter.ofPattern(
                            "HH:mm",
                            Locale.US
                        )
                    ) ?: "--:--",
                    color = Color.White,
                    size = 23
                )
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.2f)
            )

            Text(
                text = "الوقت المتبقي",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            PrayerClockText(
                value = countdown,
                color = Color.White,
                size = 38
            )

            if (
                next != null &&
                next.second.toLocalDate() > today.date
            ) {
                Text(
                    text = "فجر الغد",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerPlacePicker(
    colors: SalahColors,
    font: FontFamily,
    onDismiss: () -> Unit,
    onCitySelected: (String) -> Unit,
    onDefaultSelected: () -> Unit,
    onCoordinatesSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var city by rememberSaveable {
        mutableStateOf("")
    }

    var locating by remember {
        mutableStateOf(false)
    }

    var locationError by remember {
        mutableStateOf<String?>(null)
    }

    var showSettings by remember {
        mutableStateOf(false)
    }

    val latestOnCoordinatesSelected by rememberUpdatedState(
        onCoordinatesSelected
    )

    fun locate() {
        if (locating) return

        scope.launch {
            locating = true
            locationError = null
            showSettings = false

            try {
                val location = withTimeoutOrNull(35_000L) {
                    getPrayerDeviceLocation(context)
                }

                if (location == null) {
                    locationError =
                        "تعذر تحديد الموقع خلال المهلة. جرّب قرب نافذة أو اختر المدينة يدويًا."
                } else {
                    latestOnCoordinatesSelected(
                        location.latitude,
                        location.longitude
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: PrayerLocationDisabled) {
                locationError =
                    "خدمة الموقع معطلة. فعّلها ثم أعد المحاولة."
                showSettings = true
            } catch (exception: SecurityException) {
                locationError =
                    "لم يتم منح صلاحية الموقع. يمكنك اختيار المدينة يدويًا."
            } catch (exception: Exception) {
                locationError =
                    "تعذر الحصول على موقعك. تحقق من إعدادات الموقع وأعد المحاولة."
            } finally {
                locating = false
            }
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true ||
                        permissions[
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ] == true

            if (granted) {
                locate()
            } else {
                locationError =
                    "تم رفض إذن الموقع. يمكنك إدخال المدينة يدويًا أو منح الإذن من إعدادات التطبيق."
            }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 28.dp
                ),
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "اختيار المكان",
                        color = colors.text,
                        fontFamily = font,
                        fontSize = 22.sp
                    )
                }

                item {
                    Text(
                        text = "يُستخدم موقعك لجلب المواقيت عند الطلب فقط. لا يوجد تتبع مستمر.",
                        color = colors.secondary,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )
                }

                item {
                    OutlinedButton(
                        enabled = !locating,
                        onClick = {
                            if (
                                hasPrayerLocationPermission(context)
                            ) {
                                locate()
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (locating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = colors.accent
                            )

                            Spacer(Modifier.width(12.dp))
                        }

                        Text(
                            text = if (locating) {
                                "جارٍ تحديد الموقع..."
                            } else {
                                "استخدام موقعي الحالي"
                            },
                            color = colors.accent
                        )
                    }
                }

                locationError?.let { message ->
                    item {
                        Text(
                            text = message,
                            color = colors.text,
                            fontSize = 13.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                if (showSettings) {
                    item {
                        TextButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                        )
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = "فتح إعدادات الموقع",
                                color = colors.accent
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "أو اختر مدينة داخل الجزائر",
                        color = colors.secondary,
                        fontSize = 13.sp
                    )
                }

                item {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {
                            city = it
                        },
                        enabled = !locating,
                        label = {
                            Text("اسم المدينة")
                        },
                        placeholder = {
                            Text("مثال: Oran أو Algiers")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colors.text,
                                unfocusedTextColor = colors.text,
                                focusedLabelColor = colors.accent,
                                unfocusedLabelColor =
                                    colors.secondary,
                                focusedBorderColor = colors.accent,
                                unfocusedBorderColor =
                                    colors.secondary,
                                cursorColor = colors.accent
                            )
                    )
                }

                item {
                    Button(
                        enabled = city.isNotBlank() && !locating,
                        onClick = {
                            onCitySelected(city.trim())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.header,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "عرض مواقيت المدينة",
                            fontFamily = font
                        )
                    }
                }

                item {
                    TextButton(
                        enabled = !locating,
                        onClick = onDefaultSelected,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "استخدام الجزائر العاصمة",
                            color = colors.accent,
                            fontFamily = font
                        )
                    }
                }
            }
        }
    }
}