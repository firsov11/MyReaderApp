package com.firsov.myreaderapp.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.firsov.myreaderapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    bookId: String,
    viewModel: MainViewModel,
    onBookUpdated: () -> Unit,
    onCancel: () -> Unit
) {
    val books by viewModel.books.collectAsState()
    val originalBook = books.find { it.id == bookId }

    var number by remember { mutableStateOf(TextFieldValue(originalBook?.number ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(originalBook?.description ?: "")) }
    var selectedGenre by remember { mutableStateOf(originalBook?.selectedGenre ?: "") }
    var nextInspectionDate by remember { mutableStateOf(originalBook?.nextInspectionDate ?: "") }

    val genreOptions = listOf("Художня", "Документація", "Технічна", "Інше")
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Редагувати картку", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Номер") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Опис") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = selectedGenre,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Тип захисного засобу") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(8.dp))

        val context = LocalContext.current
        val calendar = Calendar.getInstance()

        OutlinedTextField(
            value = nextInspectionDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата перевірки") },
            trailingIcon = {
                IconButton(onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val formatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                .format(GregorianCalendar(year, month, day).time)
                            nextInspectionDate = formatted
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Вибрати дату"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val formatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                .format(GregorianCalendar(year, month, day).time)
                            nextInspectionDate = formatted
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        )


        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onCancel) {
                Text("Скасувати")
            }

            Button(onClick = {
                if (originalBook != null) {
                    val updatedBook = originalBook.copy(
                        number = number.text,
                        description = description.text,
                        selectedGenre = selectedGenre,
                        nextInspectionDate = nextInspectionDate
                    )
                    viewModel.updateBook(updatedBook)
                    onBookUpdated()
                }
            }) {
                Text("Зберегти")
            }
        }
    }
}
