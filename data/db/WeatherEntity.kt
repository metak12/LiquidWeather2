package com.weather.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val id: Int = 1,
    val cityName: String,
    val temperature: Double,
    val humidity: Int,
    val weatherCode: Int,
    val uvIndex: Double,
    val forecastJson: String, // Сериализованные почасовые/ежедневные данные
    val lastUpdated: Long,
    val isStormWarning: Boolean
)
