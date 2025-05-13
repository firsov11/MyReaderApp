package com.firsov.myreaderapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firsov.myreaderapp.data.Book
import com.firsov.myreaderapp.ui.components.BookCard
import com.firsov.myreaderapp.viewmodel.MainViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onAddClick: (() -> Unit)? = null,
    onBookClick: (Book) -> Unit
) {
    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(                                     // 👈 ОБЕРТКА
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isLoading) {
                // Показать индикатор загрузки, если данные еще загружаются
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Отображаем список книг, если данные загружены
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                ) {
                    items(books) { book ->
                        val highlightColor = getHighlightColor(book.nextInspectionDate)
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book) },
                            highlight = highlightColor
                        )
                    }


                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onClick = {
                    if (onAddClick != null) {
                        onAddClick()
                    } else {
                        viewModel.addBook(Book("Новая книга", "Описание", "123", ""))
                    }
                }
            ) {
                Text("Створити картку")
            }
        }
    }
}

fun isInspectionOverdue(dateStr: String): Boolean {
    return try {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val inspectionDate = formatter.parse(dateStr) ?: return false
        inspectionDate.before(Calendar.getInstance().time)
    } catch (e: Exception) {
        false
    }
}

fun isInspectionSoon(dateStr: String, days: Int = 3): Boolean {
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

fun getHighlightColor(dateStr: String): Color {
    return when {
        isInspectionOverdue(dateStr) -> Color.Red
        isInspectionSoon(dateStr) -> Color(0xFFFFC107) // Жёлтый
        else -> Color.Unspecified
    }
}






