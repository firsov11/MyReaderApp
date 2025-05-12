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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firsov.myreaderapp.ui.screens.AddBookScreen
import com.firsov.myreaderapp.ui.screens.BookDetailsScreen
import com.firsov.myreaderapp.ui.screens.MainScreen
import com.firsov.myreaderapp.ui.theme.MyReaderAppTheme
import com.firsov.myreaderapp.viewmodel.MainViewModel
import java.util.*

import androidx.work.*
import com.firsov.myreaderapp.worker.ReminderWorker
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

        setContent {
            MyReaderAppTheme {
                AppNavigation()
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

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 25)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis - System.currentTimeMillis()
    }

}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            val viewModel: MainViewModel = viewModel()

            // Обновляем список книг каждый раз при входе на экран
            LaunchedEffect(Unit) {
                viewModel.fetchBooks()
            }

            MainScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("add") },
                onBookClick = { book -> navController.navigate("bookDetails/${book.id}") }
            )
        }

        composable("add") {
            AddBookScreen(onBookAdded = { navController.popBackStack() })
        }
        composable("bookDetails/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(
                bookId = bookId,
                onBookDeleted = { navController.popBackStack() }
            )
        }
    }
}
