package com.weather.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.weather.app.ui.MainActivity

class WeatherWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val size = LocalSize.current
            val isStorm = false // Читается из репозитория/стейта

            // Эффект матового полупрозрачного стекла с динамической адаптацией Material You
            val glassBackground = if (isStorm) {
                ColorProvider(android.graphics.Color.parseColor("#99331111"))
            } else {
                ColorProvider(android.graphics.Color.parseColor("#33FFFFFF")) // Полупрозрачный слой стекла
            }

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(glassBackground)
                    .padding(12.dp)
                    .clickable(actionStartActivity<MainActivity>()), // Клик открывает Activity
                contentAlignment = Alignment.Center
            ) {
                when {
                    // Минимум (2x1)
                    size.width < 180.dp || size.height < 120.dp -> {
                        CompactWeatherView(temp = "+20°", city = "Москва", icon = "⛅")
                    }
                    // Средний (2x2 или 4x2)
                    size.height < 250.dp -> {
                        MediumWeatherView(temp = "+20°", city = "Москва", humidity = "45%", condition = "Облачно")
                    }
                    // Максимальный (4x4)
                    else -> {
                        ExpandedWeatherView(
                            temp = "+20°",
                            city = "Москва",
                            humidity = "45%",
                            uvIndex = "3.2 (Умеренный)",
                            summary = "Днем комфортная погода, без осадков. Ветер слабый."
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun CompactWeatherView(temp: String, city: String, icon: String) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(text = icon, style = TextStyle(fontSize = 24.sp))
            Spacer(modifier = GlanceModifier.width(8.dp))
            Column {
                Text(text = temp, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorProvider(android.graphics.Color.WHITE)))
                Text(text = city, style = TextStyle(fontSize = 12.sp, color = ColorProvider(android.graphics.Color.LIGHT_GRAY)))
            }
        }
    }

    @Composable
    fun MediumWeatherView(temp: String, city: String, humidity: String, condition: String) {
        Column(modifier = GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.Vertical.CenterVertically) {
            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.Horizontal.Start) {
                Column {
                    Text(text = city, style = TextStyle(fontSize = 14.sp, color = ColorProvider(android.graphics.Color.LIGHT_GRAY)))
                    Text(text = temp, style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = ColorProvider(android.graphics.Color.WHITE)))
                }
            }
            Spacer(modifier = GlanceModifier.height(8.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(text = condition, style = TextStyle(fontSize = 13.sp, color = ColorProvider(android.graphics.Color.WHITE)))
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(text = "Влажность: $humidity", style = TextStyle(fontSize = 12.sp, color = ColorProvider(android.graphics.Color.LIGHT_GRAY)))
            }
        }
    }

    @Composable
    fun ExpandedWeatherView(temp: String, city: String, humidity: String, uvIndex: String, summary: String) {
        Column(modifier = GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.Vertical.CenterVertically) {
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Column {
                    Text(text = city, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = ColorProvider(android.graphics.Color.WHITE)))
                    Text(text = temp, style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = ColorProvider(android.graphics.Color.WHITE)))
                }
            }
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(text = summary, style = TextStyle(fontSize = 12.sp, color = ColorProvider(android.graphics.Color.WHITE)))
            Spacer(modifier = GlanceModifier.defaultWeight())
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(text = "УФ-индекс: $uvIndex", style = TextStyle(fontSize = 11.sp, color = ColorProvider(android.graphics.Color.LIGHT_GRAY)))
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(text = "Вл: $humidity", style = TextStyle(fontSize = 11.sp, color = ColorProvider(android.graphics.Color.LIGHT_GRAY)))
            }
        }
    }
}

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()
}
