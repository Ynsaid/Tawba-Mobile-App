import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.ViewModals.DarkModeViewModel
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Bg_Prayer
import com.example.tawbaapp.ui.theme.Green

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AdhkarsPage(navController: NavController,darkModeViewModel: DarkModeViewModel) {
    val amiri = FontFamily(Font(R.font.amiri_regular))
    // اذكار list
    val adhkarItems = listOf(
        Triple("أذكار الصباح", R.drawable.sun_svg_orange_icon_, Color(0xFFB2F0B2)),
        Triple("أذكار المساء", R.drawable.moon_svg_icon, Color(0xFFCCE5FF)),
        Triple("بعد الصلاة", R.drawable.pray_svg_icon, Color(0xFFFFD6A5)),
        Triple("قبل النوم", R.drawable.sleeping_svg_icon, Color(0xFFFFB5E8)),
        Triple("الاستيقاظ", R.drawable.getup_svg_icon, Color(0xFFDFD7FF)),
        Triple("الطعام", R.drawable.eat_svg_icon, Color(0xFFFFC6C6))
    )
    // ادعية list
    val adiyaItems = listOf(
        Triple("دعاء الرزق", R.drawable.rizq_svg_icon, Color(0xFFCCE5FF)),
        Triple("دعاء الشفاء", R.drawable.shifa_svg_icon, Color(0xFFFFB5E8)),
        Triple("دعاء التوبة", R.drawable.tawba_svg_icon, Color(0xFFFFD6A5)),
        Triple("دعاء الكرب", R.drawable.karb_svg_icon, Color(0xFFB2F0B2)),
        Triple("دعاء المطر", R.drawable.matar_svg_icon, Color(0xFFDFD7FF)),
        Triple("دعاء الاستخارة", R.drawable.istikhara_svg_icon, Color(0xFFFFC6C6))
    )

    val isDark by darkModeViewModel.isDark.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Black else Color.White)
    ) {
        item {
            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left_icon),
                        contentDescription = "Back",
                        tint = Green
                    )
                }
            }

            // Title: أذكار
            Text(
                text = "أذكار",
                fontSize = 32.sp,
                fontFamily = amiri,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp,end = 16.dp),
                color = Green
            )

            // Grid: أذكار
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .height(300.dp)
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                userScrollEnabled = false
            ) {
                itemsIndexed(adhkarItems) { _, (title, image) ->
                    AdhkarItem(title = title, color = Bg_Prayer, image = image){
                        navController.navigate("adhkar_detail/$title")
                    }
                }
            }

            // Title: أدعية
            Text(
                text = "أدعية",
                fontSize = 32.sp,
                fontFamily = amiri,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp,end = 16.dp),
                color = Green
            )

            // Grid: أدعية
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .height(300.dp)
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                userScrollEnabled = false
            ) {
                itemsIndexed(adiyaItems) { _, (title, image) ->
                    AdhkarItem(title = title, color = Bg_Prayer, image = image){
                        navController.navigate("adhkar_detail/$title")
                    }
                }
            }
        }
    }
}

@Composable
fun AdhkarItem(title: String, color: Color, image: Int,onClick:()->Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color)
                .clickable { onClick() }, // Handle click
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = Green,
            fontFamily = FontFamily(Font(R.font.amiri_regular)),
            textAlign = TextAlign.Center
        )
    }
}
