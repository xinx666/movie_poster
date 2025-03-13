package com.example.movieposter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movieposter.data.Cinema
import com.example.movieposter.data.CinemaParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cinemas = remember { mutableStateOf<List<Cinema>>(emptyList()) }
            val isLoading = remember { mutableStateOf(true) }

            // Запускаем асинхронную задачу для парсинга
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    // Используем CinemaParser для получения данных
                    val parser = CinemaParser()
                    val cinemaList = parser.parseCinemas()

                    // Обновляем UI с результатами
                    cinemas.value = cinemaList
                    isLoading.value = false
                } catch (e: Exception) {
                    Log.e("com.example.movieposter.MainActivity", "Error while parsing", e)
                    isLoading.value = false
                }
            }

            CinemaScreen(cinemas.value, isLoading.value)
        }
    }
}

@Composable
fun CinemaScreen(cinemas: List<Cinema>, isLoading: Boolean) {
    Column(modifier = Modifier) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (cinemas.isEmpty()) {
                Text(text = "Нет данных о кинотеатрах.")
            } else {
                cinemas.forEach { cinema ->
                    Text(text = "Название: ${cinema.name}")
                    Text(text = "Станция метро: ${cinema.metroStation}")
                    Text(text = "Ссылка на расписание: ${cinema.scheduleUrl}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
