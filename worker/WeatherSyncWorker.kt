package com.weather.app.worker

import android.content.Context
import androidx.work.*
import com.weather.app.data.db.WeatherDatabase
import com.weather.app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class WeatherSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = WeatherDatabase.getDatabase(applicationContext)
            val repository = WeatherRepository(database.weatherDao())
            repository.refreshWeather(55.751244, 37.618423) // Координаты по умолчанию
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun enqueuePeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Умное расписание: Wi-Fi / сеть
                .build()

            val workRequest = PeriodicWorkRequestBuilder<WeatherSyncWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WeatherSyncWork",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}

class WeatherApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        WeatherSyncWorker.enqueuePeriodicWork(this)
    }
}
