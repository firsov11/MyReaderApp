package com.firsov.myreaderapp.viewmodel

import androidx.lifecycle.ViewModel
import com.firsov.myreaderapp.data.Book
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
            val booksList = result.map { doc ->
                Book(
                    id = doc.id, // 🔥 это ключевой момент
                    number = doc.getString("number") ?: "",
                    description = doc.getString("description") ?: "",
                    selectedGenre = doc.getString("selectedGenre") ?: "",
                    nextInspectionDate = doc.getString("nextInspectionDate") ?: ""
                )
            }
            _books.value = booksList
            _isLoading.value = false
        }.addOnFailureListener {
            _isLoading.value = false
        }
    }

    fun addBook(book: Book, onSuccess: () -> Unit = {}) {
        val newDocRef = db.collection("books").document()
        val bookWithId = book.copy(id = newDocRef.id)

        newDocRef.set(bookWithId)
            .addOnSuccessListener {
                fetchBooks() // обновим список
                onSuccess()
            }
    }

    fun deleteBook(bookId: String) {
        db.collection("books").whereEqualTo("id", bookId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }

    fun updateBook(updatedBook: Book) {
        db.collection("books").whereEqualTo("id", updatedBook.id).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.set(updatedBook)
                }
            }
    }
}
