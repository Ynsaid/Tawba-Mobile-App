package com.example.tawbaapp.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tawbaapp.R
import com.example.tawbaapp.ui.theme.Black
import com.example.tawbaapp.ui.theme.Green
import kotlinx.coroutines.delay

@Composable
fun HomePage(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 124.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(324.dp)
                .height(360.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Green),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.qr),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "توبة",
            fontFamily = FontFamily(Font(R.font.cairo_blod)),
            fontSize = 32.sp,
            color =  Green
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "إستمع و إقرا القران مع الاذكار يوميا",
            fontFamily = FontFamily(Font(R.font.cairo_medium)),
            fontSize = 24.sp,
            color = Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        StartButton(navController)
    }
}

@Composable
fun StartButton(navController: NavController) {
    var moveRight by remember { mutableStateOf(true) }
    val offsetX by animateDpAsState(
        targetValue = if (moveRight) 8.dp else 0.dp,
        animationSpec = tween(500),
        label = "ArrowSlide"
    )

    LaunchedEffect(Unit) {
        while (true) {
            moveRight = !moveRight
            delay(600)
        }
    }

    Button(
        onClick = {
            navController.navigate("section")
        },
        modifier = Modifier
            .height(54.dp)
            .width(200.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Green,
            contentColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "إبدا الآن",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.cairo_blod)),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Image(
                painter = painterResource(id = R.drawable.arrow_right_icon),
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = offsetX)
                    .align(Alignment.CenterVertically)
            )
        }
    }

}
