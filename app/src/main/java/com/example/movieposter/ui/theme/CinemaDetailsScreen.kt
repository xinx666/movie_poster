import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.movieposter.data.CinemaParser
import com.example.movieposter.data.MovieSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CinemaDetailsScreen(scheduleUrl: String) {
    var address by remember { mutableStateOf("Загрузка...") }
    var description by remember { mutableStateOf("") }
    var sessions by remember { mutableStateOf<List<MovieSession>>(emptyList()) }

    LaunchedEffect(scheduleUrl) {
        try {
            val parser = CinemaParser()
            val details = withContext(Dispatchers.IO) {
                parser.fetchCinemaDetails(scheduleUrl)
            }
            address = details.first ?: "Адрес не найден"
            description = details.second ?: "Описание отсутствует"
            sessions = details.third
        } catch (e: Exception) {
            address = "Ошибка: ${e.message}"
        }
    }

    LazyColumn {
        item {
            Text(text = "Адрес: $address")
            Text(text = "Описание: $description")
            Text(text = "Расписание:")
        }
        items(sessions) { session ->
            Text(
                text = "${session.movieName} - ${session.time} - ${session.price}",
                modifier = Modifier
            )
        }
    }
}