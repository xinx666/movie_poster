import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

// Функция для получения HTML страницы по URL
fun fetchCinemaDetails(scheduleUrl: String): String {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(scheduleUrl)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch data: ${response.code}")
        }

        val htmlContent = response.body?.string() ?: ""
        return htmlContent
    }
}

// Функция для парсинга описания кинотеатра и расписания сеансов
fun parseCinemaDetails(html: String): String {
    val document = Jsoup.parse(html)

    // Извлечение данных: описание, адрес, расписание сеансов
    val cinemaName = document.select(".universalHat_title").text() // Название кинотеатра
    val cinemaDescription = document.select(".theaterInfo_descEditor more_content visualEditorInsertion").text() // Описание кинотеатра
    val cinemaAddress = document.select(".theaterInfo_dataCell").text() // Адрес кинотеатра

    // Собираем всё в одну строку для отображения
    return """
        Cinema: $cinemaName
        Description: $cinemaDescription
        Address: $cinemaAddress
    """.trimIndent()
}
