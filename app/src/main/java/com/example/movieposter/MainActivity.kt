package com.example.movieposter

import com.example.movieposter.data.Cinema
import CinemaDetailsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieposter.data.CinemaParser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "list") {
                composable("list") {
                    CinemaListScreen(navController)
                }
                composable("cinema_details/{scheduleUrl}") { backStackEntry ->
                    val encodedUrl = backStackEntry.arguments?.getString("scheduleUrl")
                    val scheduleUrl = encodedUrl?.let { java.net.URLDecoder.decode(it, "UTF-8") }
                    scheduleUrl?.let {
                        CinemaDetailsScreen(it)
                    } ?: Text("Error: Invalid URL")
                }
            }
        }
    }
}

@Composable
fun CinemaListScreen(navController: NavController) {
    var cinemas by remember { mutableStateOf<List<Cinema>>(emptyList()) }

    // Загрузка данных кинотеатров
    LaunchedEffect(Unit) {
        cinemas = CinemaParser().parseCinemas() // Вызываем функцию для получения списка кинотеатров
    }

    // Отображаем список кинотеатров
    Column(modifier = Modifier.padding(16.dp)) {
        cinemas.forEach { cinema ->
            Button(onClick = {
                val encodedUrl = java.net.URLEncoder.encode(cinema.scheduleUrl, "UTF-8")
                navController.navigate("cinema_details/$encodedUrl")
            }) {
                Text(text = cinema.name)
            }
        }
    }
}
