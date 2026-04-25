package com.example.gemainventory

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gemainventory.data.worker.SyncWorker
import java.util.concurrent.TimeUnit

class GemaApplication : Application() {

    companion object {
        lateinit var instance: GemaApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupWorkManager()
    }

    private fun setupWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Sincronizar al menos cada 15 minutos en segundo plano (límite mínimo de Android)
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncGemaInventory",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
