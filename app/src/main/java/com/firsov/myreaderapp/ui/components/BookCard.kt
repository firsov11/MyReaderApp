package com.firsov.myreaderapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.firsov.myreaderapp.data.Book

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(text = book.selectedGenre)
                Spacer(modifier = Modifier.width(12.dp)) // расстояние между текстами
                Text(text = book.description)
            }
            Text(text = book.nextInspectionDate)
            // и другие поля
        }
    }
}

