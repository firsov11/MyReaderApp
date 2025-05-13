package com.firsov.myreaderapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.firsov.myreaderapp.R
import com.firsov.myreaderapp.data.Book
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val result = Firebase.firestore.collection("books").get().await()
            val books = result.toObjects(Book::class.java)

            books.forEach { book ->
                val date = book.nextInspectionDate
                if (date == today || isWithinDays(date, 3)) {
                    showNotification("Наближається дата перевірки для: ${book.selectedGenre} ${book.description}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun isWithinDays(dateStr: String, days: Int): Boolean {
        return try {
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val inspectionDate = formatter.parse(dateStr) ?: return false
            val today = Calendar.getInstance().time
            val diff = inspectionDate.time - today.time
            diff in 0..(days * 24 * 60 * 60 * 1000L)
        } catch (e: Exception) {
            false
        }
    }

    private fun showNotification(message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "inspection_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Нагадування", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
