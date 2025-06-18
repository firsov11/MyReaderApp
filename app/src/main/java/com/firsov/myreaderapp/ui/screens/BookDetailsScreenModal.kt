package com.firsov.myreaderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firsov.myreaderapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreenModal(
    bookId: String,
    viewModel: MainViewModel = viewModel(),
    onDismiss: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val books by viewModel.books.collectAsState()
    val book = books.find { it.id == bookId }
    val isLoading by viewModel.isLoading.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onDismiss)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (book != null) {
                    Column {
                        Text("Картка обліку", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Тип: ${book.selectedGenre}")
                        Spacer(Modifier.height(4.dp))
                        Text("Номер: ${book.number}")
                        Spacer(Modifier.height(4.dp))
                        Text("Опис: ${book.description}")
                        Spacer(Modifier.height(4.dp))
                        Text("Дата наступної перевірки: ${book.nextInspectionDate}")

                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { onEditClick(book.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Редагувати")
                            }

                            IconButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Видалити")
                            }
                        }

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Видалення", color = MaterialTheme.colorScheme.onSurface) },
                                text = { Text("Ви впевнені, що хочете видалити картку?", color = MaterialTheme.colorScheme.onSurface) },
                                confirmButton = {
                                    TextButton(onClick = {
                                        viewModel.deleteBook(book.id)
                                        showDialog = false
                                        onDismiss()
                                    }) {
                                        Text("Видалити", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("Відміна", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            )
                        }

                    }
                } else {
                    Text("Картку не знайдено.")
                }
            }
        }
    }
}




