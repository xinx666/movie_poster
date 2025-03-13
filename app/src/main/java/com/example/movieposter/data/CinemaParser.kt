package com.example.movieposter.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class CinemaParser {

    suspend fun parseCinemas(): List<Cinema> {
        val url = "https://nsk.kinoafisha.info/"
        val cinemas = mutableListOf<Cinema>()

        try {
            val document = withContext(Dispatchers.IO) {
                Jsoup.connect(url)
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
                    )
                    .get()
            }

            val cinemaElements = document.select(".cinemaList_item")
            for (cinemaElement in cinemaElements) {
                val name = cinemaElement.select(".cinemaList_name").text()
                val metroStation = cinemaElement.select(".cinemaList_metroItem").text()
                val scheduleUrl = cinemaElement.select(".cinemaList_ref").attr("href")

                val fullCinemaInfo = fetchCinemaDetails(scheduleUrl)

                cinemas.add(
                    Cinema(
                        name = name,
                        metroStation = metroStation,
                        scheduleUrl = scheduleUrl,
                        fullAddress = fullCinemaInfo.first,
                        description = fullCinemaInfo.second,
                        schedule = fullCinemaInfo.third
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("CinemaParser", "Error while parsing", e)
        }

        return cinemas
    }

    suspend fun fetchCinemaDetails(scheduleUrl: String): Triple<String?, String?, List<MovieSession>> {
        return try {
            val document = withContext(Dispatchers.IO) {
                Jsoup.connect(scheduleUrl)
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
                    )
                    .get()
            }

            // Адрес и описание
            val address = document.select(".cinema-address").text()
            val description = document.select(".cinema-description").text()

            // Парсинг сеансов
            val sessions = mutableListOf<MovieSession>()
            val showtimeItems = document.select(".showtimes_item")
            for (item in showtimeItems) {
                val movieName = item.select(".showtimes_name").text()
                val posterUrl = item.select(".showtimes_posterImage img").attr("src")
                val sessionElements = item.select(".showtimes_session")

                for (session in sessionElements) {
                    val time = session.select(".session_time").text()
                    val price = session.select(".session_price").text()

                    sessions.add(
                        MovieSession(
                            movieName = movieName,
                            time = time,
                            price = price,
                            posterUrl = posterUrl.takeIf { it.isNotEmpty() }
                        )
                    )
                }
            }

            Triple(address, description, sessions)
        } catch (e: Exception) {
            Log.e("CinemaParser", "Error while fetching cinema details", e)
            Triple(null, null, emptyList())
        }
    }
}