package com.firsov.myreaderapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firsov.myreaderapp.data.Book
import com.firsov.myreaderapp.ui.components.BookCard
import com.firsov.myreaderapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onAddClick: (() -> Unit)? = null,
    onBookClick: (Book) -> Unit,
    onLogoutClick: (() -> Unit)? = null
) {
    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    if (onAddClick != null) {
                        onAddClick()
                    } else {
                        viewModel.addBook(Book("Новая книга", "Описание", "123", ""))
                    }
                }) {
                    Text("Створити картку")
                }

                Button(onClick = { onLogoutClick?.invoke() }) {
                    Text("Вийти")
                }
            }
        }
    }
}

fun isInspectionOverdue(dateStr: String): Boolean {
    return try {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val inspectionDate = formatter.parse(dateStr) ?: return false
        inspectionDate.before(Calendar.getInstance().time)
    } catch (e: Exception) {
        false
    }
}

fun isInspectionSoon(dateStr: String, days: Int = 3): Boolean {
    return try {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
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