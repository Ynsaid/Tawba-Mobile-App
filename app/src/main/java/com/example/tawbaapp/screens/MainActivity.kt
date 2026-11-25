package com.example.tawbaapp.screens

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tawbaapp.R
import com.example.tawbaapp.viewmodals.DarkModeViewModel
import com.example.tawbaapp.viewmodals.ReadingProgressViewModel
import com.example.tawbaapp.classes.AppPreferences
import com.example.tawbaapp.classes.ReaderPreference
import com.example.tawbaapp.classes.Surah
import com.example.tawbaapp.ui.theme.TawbaAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TawbaApp)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val inputStream = resources.openRawResource(R.raw.quran_en)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val surahListType = object : TypeToken<List<Surah>>() {}.type
        val surahList: List<Surah> = Gson().fromJson(jsonString, surahListType)

        val appPrefs = AppPreferences(this)

        setContent {
            TawbaAppTheme {
                val navController = rememberNavController()
                val darkModeViewModel: DarkModeViewModel = viewModel()
                val readingProgressViewModel: ReadingProgressViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                val nextDestination = if (appPrefs.isFirstLaunch()) {
                                    appPrefs.setFirstLaunchDone()
                                    "home"
                                } else {
                                    "section"
                                }
                                navController.navigate(nextDestination) {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }
                        composable("home") {
                            HomePage(navController)
                        }
                        composable("section") {
                            SectionPage(
                                navController = navController,
                                darkModeViewModel = darkModeViewModel,
                                readingProgressViewModel = readingProgressViewModel,
                                surahList = surahList
                            )
                        }
                        composable("reading") {
                            ReadingPage(
                                navController = navController,
                                surahs = surahList,
                                darkModeViewModel = darkModeViewModel
                            )
                        }
                        composable(
                            route = "surah/{surahJson}",
                            arguments = listOf(
                                navArgument("surahJson") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val surahJsonEncoded =
                                backStackEntry.arguments?.getString("surahJson") ?: ""

                            val surahJson = URLDecoder.decode(surahJsonEncoded, "UTF-8")
                            val surah = Gson().fromJson(surahJson, Surah::class.java)

                            SurahPage(
                                navController = navController,
                                surah = surah,
                                darkModeViewModel = darkModeViewModel,
                                readingProgressViewModel = readingProgressViewModel
                            )
                        }

                        composable(
                            route = "listing/{readerId}",
                            arguments = listOf(
                                navArgument("readerId") {
                                    defaultValue = "default"
                                }
                            )
                        ) { backStackEntry ->

                            val passedReader =
                                backStackEntry.arguments?.getString("readerId") ?: "default"

                            var readerIdState by remember(passedReader) {
                                mutableStateOf<String?>(if (passedReader != "default") passedReader else null)
                            }
                            LaunchedEffect(passedReader) {
                                if (passedReader == "default") {
                                    readerIdState = ReaderPreference(this@MainActivity).getReaderId()
                                }
                            }

                            readerIdState?.let { readerId ->
                                ListeningPage(
                                    navController = navController,
                                    darkModeViewModel = darkModeViewModel,
                                    readerId = readerId
                                )
                            }
                        }

                        composable("prayers") {
                            PrayersPage(
                                navController = navController,
                                darkModeViewModel = darkModeViewModel

                            )
                        }

                        composable("adhkars") {
                            AdhkarsPage(navController, darkModeViewModel)
                        }

                        composable("adhkar_detail/{title}") { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: ""
                            AdhkarDetailPage(title, navController, darkModeViewModel)
                        }
                    }
                }
            }
        }
    }

