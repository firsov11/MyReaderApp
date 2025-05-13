package com.firsov.myreaderapp.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.firsov.myreaderapp.data.Book
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(onBookAdded: () -> Unit) {
    val context = LocalContext.current
    val db = Firebase.firestore

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf("") }
    var nextInspectionDate by remember { mutableStateOf("") }
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
            nextInspectionDate = selectedDate
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Surface(                                     // 👈 ОБЕРТКА
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Картка обліку", style = MaterialTheme.typography.headlineMedium)

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
                value = nextInspectionDate,
                onValueChange = { },
                label = { Text("Дата наступної перевірки") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,  // Делаем поле только для чтения
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Choose Date"
                        ) // Используем DateRange
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank() || selectedGenre.isBlank() || nextInspectionDate.isBlank()) {
                        Toast.makeText(context, "Заповніть усі поля", Toast.LENGTH_SHORT).show()
                    } else {
                        val newBook = Book(
                            name = name,
                            description = description,
                            selectedGenre = selectedGenre,
                            nextInspectionDate = nextInspectionDate
                        )

                        db.collection("books").add(newBook).addOnSuccessListener {
                            Toast.makeText(context, "Картка створена", Toast.LENGTH_SHORT).show()
                            onBookAdded()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зберегти")
            }
        }
    }
}
