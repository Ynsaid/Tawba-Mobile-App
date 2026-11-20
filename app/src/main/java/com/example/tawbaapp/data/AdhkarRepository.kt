package com.example.tawbaapp.data

import com.example.tawbaapp.R
import com.example.tawbaapp.classes.AdhkarCategory
import com.example.tawbaapp.classes.AdhkarType
import androidx.compose.ui.graphics.Color

object AdhkarRepository {

    val adhkarCategories = listOf(
        AdhkarCategory(
            id = "morning",
            title = "أذكار الصباح",
            subtitle = "أذكار بداية اليوم",
            iconRes = R.drawable.sun_svg_orange_icon_,
            colorRes = Color(0xFFFFF3D6),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "evening",
            title = "أذكار المساء",
            subtitle = "أذكار نهاية اليوم",
            iconRes = R.drawable.moon_svg_icon,
            colorRes = Color(0xFFE3F0FF),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "after_prayer",
            title = "بعد الصلاة",
            subtitle = "أذكار ما بعد الصلاة",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F7EA),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "before_sleep",
            title = "قبل النوم",
            subtitle = "أذكار النوم",
            iconRes = R.drawable.sleeping_svg_icon,
            colorRes = Color(0xFFFFE9F4),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "waking_up",
            title = "الاستيقاظ",
            subtitle = "أذكار بداية اليوم",
            iconRes = R.drawable.getup_svg_icon,
            colorRes = Color(0xFFEDE7FF),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "food",
            title = "الطعام",
            subtitle = "أذكار الأكل والشرب",
            iconRes = R.drawable.eat_svg_icon,
            colorRes = Color(0xFFFFE7E7),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "entering_home",
            title = "دخول المنزل",
            subtitle = "أذكار دخول البيت",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F0FF),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "leaving_home",
            title = "الخروج من المنزل",
            subtitle = "أذكار الخروج",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFFFF0E0),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "travel",
            title = "السفر",
            subtitle = "أذكار السفر",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F7F7),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "rain",
            title = "المطر",
            subtitle = "أذكار المطر",
            iconRes = R.drawable.matar_svg_icon,
            colorRes = Color(0xFFE3F0FF),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "entering_mosque",
            title = "دخول المسجد",
            subtitle = "أذكار المسجد",
            iconRes = R.drawable.mosque_svg_icon,
            colorRes = Color(0xFFE7F7EA),
            type = AdhkarType.ADHKAR
        ),
        AdhkarCategory(
            id = "leaving_mosque",
            title = "الخروج من المسجد",
            subtitle = "أذكار الخروج",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F7EA),
            type = AdhkarType.ADHKAR
        )
    )

    val duaCategories = listOf(
        AdhkarCategory(
            id = "rizq",
            title = "دعاء الرزق",
            subtitle = "الدعاء بالرزق والسعة",
            iconRes = R.drawable.rizq_svg_icon,
            colorRes = Color(0xFFE3F0FF),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "shifa",
            title = "دعاء الشفاء",
            subtitle = "الدعاء بالشفاء",
            iconRes = R.drawable.shifa_svg_icon,
            colorRes = Color(0xFFFFE9F4),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "tawba",
            title = "دعاء التوبة",
            subtitle = "الدعاء بالتوبة",
            iconRes = R.drawable.tawba_svg_icon,
            colorRes = Color(0xFFFFF0E0),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "karb",
            title = "دعاء الكرب",
            subtitle = "الدعاء عند الضيق",
            iconRes = R.drawable.karb_svg_icon,
            colorRes = Color(0xFFE7F7EA),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "rain_dua",
            title = "دعاء المطر",
            subtitle = "الدعاء عند المطر",
            iconRes = R.drawable.matar_svg_icon,
            colorRes = Color(0xFFE3F0FF),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "istikhara",
            title = "دعاء الاستخارة",
            subtitle = "الاستخارة في الأمور",
            iconRes = R.drawable.istikhara_svg_icon,
            colorRes = Color(0xFFFFE7E7),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "forgiveness",
            title = "دعاء المغفرة",
            subtitle = "الدعاء بالعفو",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F7EA),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "guidance",
            title = "دعاء الهدى",
            subtitle = "الدعاء بالهداية",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFEDE7FF),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "protection",
            title = "دعاء الحفظ",
            subtitle = "الدعاء بالحفظ",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F0FF),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "parents",
            title = "دعاء الوالدين",
            subtitle = "الدعاء للوالدين",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFFFF0E0),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "knowledge",
            title = "دعاء العلم",
            subtitle = "الدعاء بالعلم",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F7F7),
            type = AdhkarType.DUA
        ),
        AdhkarCategory(
            id = "death",
            title = "دعاء الميت",
            subtitle = "الدعاء للمتوفى",
            iconRes = R.drawable.pray_svg_icon,
            colorRes = Color(0xFFE7F0FF),
            type = AdhkarType.DUA
        )
    )

    fun search(
        query: String
    ): List<AdhkarCategory> {
        val normalizedQuery = query
            .trim()
            .lowercase()

        if (normalizedQuery.isBlank()) {
            return adhkarCategories + duaCategories
        }

        return (adhkarCategories + duaCategories).filter {
            it.title.contains(
                normalizedQuery,
                ignoreCase = true
            ) ||
                    it.subtitle.contains(
                        normalizedQuery,
                        ignoreCase = true
                    )
        }
    }
}