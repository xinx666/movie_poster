import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import fetchCinemaDetails
import parseCinemaDetails

@Composable
fun CinemaDetailsScreen(scheduleUrl: String) {
    var cinemaDetails by remember { mutableStateOf("Загрузка...") }

    LaunchedEffect(scheduleUrl) {
        try {
            val htmlContent = withContext(Dispatchers.IO) {
                fetchCinemaDetails(scheduleUrl)
            }
            cinemaDetails = parseCinemaDetails(htmlContent)
        } catch (e: Exception) {
            cinemaDetails = "Ошибка: ${e.message}"
        }
    }

    Text(text = cinemaDetails)
}
