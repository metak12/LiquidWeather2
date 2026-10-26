package com.weather.app.data.repository

import com.google.gson.Gson
import com.weather.app.data.api.OpenMeteoApi
import com.weather.app.data.db.WeatherDao
import com.weather.app.data.db.WeatherEntity
import kotlinx.coroutines.flow.Flow

class WeatherRepository(private val dao: WeatherDao, private val api: OpenMeteoApi = OpenMeteoApi.instance) {
    
    val currentWeather: Flow<WeatherEntity?> = dao.getCachedWeather()

    suspend fun refreshWeather(lat: Double, lon: Double, cityName: String = "Москва") {
        try {
            val response = api.getWeatherData(lat, lon)
            val current = response.current
            val daily = response.daily
            
            if (current != null) {
                val isStorm = current.weatherCode >= 95 || current.windSpeed > 15.0
                val uvMax = daily?.uvIndexMax?.firstOrNull() ?: 0.0

                val entity = WeatherEntity(
                    cityName = cityName,
                    temperature = current.temperature,
                    humidity = current.humidity,
                    weatherCode = current.weatherCode,
                    uvIndex = uvMax ?: 0.0,
                    forecastJson = Gson().toJson(response),
                    lastUpdated = System.currentTimeMillis(),
                    isStormWarning = isStorm
                )
                dao.insertWeather(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Работает оффлайн на закешированных данных
        }
    }
}
