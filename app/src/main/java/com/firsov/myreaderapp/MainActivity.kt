package com.firsov.myreaderapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.firsov.myreaderapp.navigation.AppNavigation
import com.firsov.myreaderapp.ui.theme.MyReaderAppTheme
import com.firsov.myreaderapp.viewmodel.MainViewModel
import com.firsov.myreaderapp.worker.ReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Запрос разрешения
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            Toast.makeText(
                this,
                if (isGranted) "Разрешение на уведомления получено" else "Разрешение на уведомления отклонено",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 🔄 Новый способ — запускаем WorkManager
        scheduleReminderWorker()

        //runReminderCheckNow()

        setContent {
            MyReaderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // 👈 тут задаётся глобальный фон
                ) {
                    val mainViewModel: MainViewModel by viewModels()
                    AppNavigation(mainViewModel)
                }
            }
        }

    }

    private fun scheduleReminderWorker() {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ReminderWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun runReminderCheckNow() {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>().build()
        WorkManager.getInstance(this).enqueue(request)
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis - System.currentTimeMillis()
    }

}



