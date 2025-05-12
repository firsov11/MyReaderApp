package com.firsov.myreaderapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.myreaderapp.data.Book
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val db = Firebase.firestore

    init {
        fetchBooks()
    }

    fun fetchBooks() {
        _isLoading.value = true
        db.collection("books").get().addOnSuccessListener { result ->
            _books.value = result.toObjects(Book::class.java)
            _isLoading.value = false
        }.addOnFailureListener {
            _isLoading.value = false
        }
    }

    fun addBook(book: Book) {
        db.collection("books").document().set(book)
    }

    fun deleteBook(bookId: String) {
        db.collection("books").whereEqualTo("id", bookId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }
}
