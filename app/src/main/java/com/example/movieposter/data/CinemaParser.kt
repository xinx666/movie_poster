package com.example.movieposter.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class CinemaParser {

    // Функция для парсинга списка кинотеатров
    suspend fun parseCinemas(): List<Cinema> {
        val url = "https://nsk.kinoafisha.info/"
        val cinemas = mutableListOf<Cinema>()

        try {
            // Выполнение запроса и парсинг страницы
            val document = withContext(Dispatchers.IO) {
                Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .get()
            }

            // Парсинг элементов с классом cinemaList_item
            val cinemaElements = document.select(".cinemaList_item")
            for (cinemaElement in cinemaElements) {
                val name = cinemaElement.select(".cinemaList_name").text()
                val metroStation = cinemaElement.select(".cinemaList_metroItem").text()
                val scheduleUrl = cinemaElement.select(".cinemaList_ref").attr("href")

                cinemas.add(Cinema(name, metroStation, scheduleUrl))
            }

        } catch (e: Exception) {
            Log.e("CinemaParser", "Error while parsing", e)
        }

        return cinemas
    }
}