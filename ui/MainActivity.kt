package com.weather.app.ui

osport android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.app.data.db.WeatherDatabase
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.ui.theme.WeatherAppTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = WeatherRepository(WeatherDatabase.getDatabase(this).weatherDao())

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherDetailScreen(repository)
                }
            }
        }
    }
}

@Composable
fun WeatherDetailScreen(repository: WeatherRepository) {
    var currentWeather by remember { mutableStateOf<com.weather.app.data.db.WeatherEntity?>(null) }

    LaunchedEffect(Unit) {
        repository.currentWeather.collectLatest {
            currentWeather = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2C3E50), Color(0xFF000000))
                )
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = currentWeather?.cityName ?: "Москва",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Главная карточка с градиентом
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "${currentWeather?.temperature ?: 20}°C",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Влажность: ${currentWeather?.humidity ?: 45}% | УФ-индекс: ${currentWeather?.uvIndex ?: 3.0}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Прогноз на 7 дней", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            // Детальные карточки прогноза на неделю
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(7) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "День ${index + 1}", color = Color.White, fontSize = 16.sp)
                            Text(text = "⛅ +${20 + index}° / +${15 + index}°", color = Color.LightGray, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
