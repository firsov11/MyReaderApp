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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.firsov.myreaderapp.data.Book

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    highlight: Color = Color.Unspecified
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (highlight != Color.Unspecified) highlight else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(text = book.selectedGenre)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "# ${book.number}")
            }
            Text(text = book.nextInspectionDate)
            // и другие поля
        }
    }
}

