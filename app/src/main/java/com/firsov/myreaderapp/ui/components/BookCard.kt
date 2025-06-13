package com.firsov.myreaderapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.firsov.myreaderapp.data.Book

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    highlight: Color = Color.Unspecified
) {
    // Определяем цвет карточки:
    val cardColor = when {
        !book.isActive -> Color.Gray
        highlight != Color.Unspecified -> highlight
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    println("BookCard: id=${book.id}, isActive=${book.isActive}")

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(text = book.selectedGenre)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "# ${book.number}")
            }
            Text(text = book.nextInspectionDate)
        }
    }
}
