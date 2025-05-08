package com.firsov.myreaderapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onAddClick: (() -> Unit)? = null
) {
    val books by viewModel.books.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            items(books) { book ->
                BookCard(book = book)
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
                    // fallback для тестов
                    viewModel.addBook(Book("Новая книга", "Описание", "123", ""))
                }
            }
        ) {
            Text("Добавить книгу")
        }
    }
}


