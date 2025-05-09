package com.firsov.myreaderapp.data

import java.util.UUID

data class Book (
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val selectedGenre: String = "",
    val nextInspectionDate: String = ""
)
