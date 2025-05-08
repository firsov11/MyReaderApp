package com.firsov.myreaderapp.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.DateRange // Используем DateRange для календаря
import androidx.compose.material.icons.Icons
import com.firsov.myreaderapp.data.Book
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(onBookAdded: () -> Unit) {
    val context = LocalContext.current
    val db = Firebase.firestore

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf("") }
    var next_inspection_date by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    val genres = listOf("Покажчик напруги", "Діелектричні рукавиці", "Діелектричні боти", "Діелектричний килимок", "Захисний щиток")

    // Current Date Picker State
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // Date Picker Dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = Calendar.getInstance()
            date.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(date.time)
            next_inspection_date = selectedDate
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Добавить книгу", style = MaterialTheme.typography.headlineMedium)

        // Выпадающий список с жанрами
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedGenre,
                onValueChange = {},
                readOnly = true,
                label = { Text("Тип захисного засобу") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(genre) },
                        onClick = {
                            selectedGenre = genre
                            expanded = false
                        }
                    )
                }
            }
        }

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Назва") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Примітки") },
            modifier = Modifier.fillMaxWidth()
        )

        // TextField для выбора даты (страница книги)
        TextField(
            value = next_inspection_date,
            onValueChange = { },
            label = { Text("Дата наступної перевірки") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,  // Делаем поле только для чтения
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Choose Date") // Используем DateRange
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isBlank() || description.isBlank() || selectedGenre.isBlank() || next_inspection_date.isBlank()) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                } else {
                    val newBook = Book(name, description, selectedGenre, next_inspection_date)
                    db.collection("books").add(newBook).addOnSuccessListener {
                        Toast.makeText(context, "Книга добавлена", Toast.LENGTH_SHORT).show()
                        onBookAdded()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}
