package com.firsov.myreaderapp.data

data class Book (
    val id: String = "",
    val number: String = "",
    val description: String = "",
    val selectedGenre: String = "",
    val nextInspectionDate: String = "",
    val isActive: Boolean = true
)
