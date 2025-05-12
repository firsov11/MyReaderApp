package com.firsov.myreaderapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firsov.myreaderapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    bookId: String,
    viewModel: MainViewModel = viewModel(),
    onBookDeleted: () -> Unit = {}
) {
    val books by viewModel.books.collectAsState()
    val book = books.find { it.id == bookId }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        // Показать индикатор загрузки, если данные еще загружаются
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        if (book != null) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Картка обліку") },
                        actions = {
                            IconButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Видалити картку")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                    Text(text = "Тип: ${book.selectedGenre}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Назва: ${book.name}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Опис: ${book.description}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Дата наступної перевірки: ${book.nextInspectionDate}")
                }
            }

            // Диалог подтверждения удаления
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Видалення") },
                    text = { Text("Ви впевнені, що хочете видалити картку?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteBook(book.id)
                            Toast.makeText(context, "Картка видалена", Toast.LENGTH_SHORT).show()
                            showDialog = false
                            onBookDeleted()
                        }) {
                            Text("Видалити")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Відміна")
                        }
                    }
                )
            }

        } else {
            Text("Картку не знайдено.")
        }
    }
}

