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

                // Получаем полную информацию о кинотеатре
                val fullCinemaInfo = fetchCinemaDetails(scheduleUrl)

                cinemas.add(Cinema(
                    name,
                    metroStation,
                    scheduleUrl,
                    fullAddress = fullCinemaInfo.first,
                    description = fullCinemaInfo.second,
                    schedule = fullCinemaInfo.third
                ))
            }

        } catch (e: Exception) {
            Log.e("CinemaParser", "Error while parsing", e)
        }

        return cinemas
    }

    // Функция для получения полной информации о кинотеатре
     suspend fun fetchCinemaDetails(scheduleUrl: String): Triple<String?, String?, List<String>> {
        return try {
            val document = withContext(Dispatchers.IO) {
                Jsoup.connect(scheduleUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .get()
            }

            // Извлекаем полное описание и адрес
            val address = document.select(".cinema-address").text()
            val description = document.select(".cinema-description").text()

            // Парсим расписание сеансов (это пример, нужно подстроить под конкретную структуру)
            val schedule = mutableListOf<String>()
            document.select(".session-time").forEach { session ->
                schedule.add(session.text())
            }

            // Возвращаем Triple с полным адресом, описанием и расписанием
            Triple(address, description, schedule)

        } catch (e: Exception) {
            Log.e("CinemaParser", "Error while fetching cinema details", e)
            Triple(null, null, emptyList()) // Если ошибка, возвращаем пустые значения
        }
    }
}
