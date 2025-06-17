package com.firsov.myreaderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.myreaderapp.data.Book
import com.google.firebase.auth.ktx.auth
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
    private val userId = Firebase.auth.currentUser?.uid

    init {
        fetchBooks()
    }

    fun fetchBooks() {
        val uid = userId ?: return
        _isLoading.value = true
        db.collection("users").document(uid).collection("books")
            .get()
            .addOnSuccessListener { result ->
                val booksList = result.map { doc ->
                    val book = Book(
                        id = doc.id,
                        number = doc.getString("number") ?: "",
                        description = doc.getString("description") ?: "",
                        selectedGenre = doc.getString("selectedGenre") ?: "",
                        nextInspectionDate = doc.getString("nextInspectionDate") ?: "",
                        isActive = doc.getBoolean("active") ?: true
                    )
                    Log.d("FirestoreBook", "id=${book.id}, isActive=${book.isActive}")
                    book
                }
                _books.value = booksList
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Ошибка загрузки книг: ${e.message}")
                _isLoading.value = false
            }
    }

    fun addBook(book: Book, onSuccess: () -> Unit = {}) {
        val uid = userId ?: return
        val newDocRef = db.collection("users").document(uid).collection("books").document()
        val bookWithId = book.copy(id = newDocRef.id)

        newDocRef.set(bookWithId)
            .addOnSuccessListener {
                fetchBooks()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Ошибка добавления книги: ${e.message}")
            }
    }

    fun deleteBook(bookId: String) {
        val uid = userId ?: return
        db.collection("users").document(uid).collection("books").document(bookId)
            .delete()
            .addOnSuccessListener {
                fetchBooks()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Ошибка удаления книги: ${e.message}")
            }
    }

    fun updateBook(updatedBook: Book) {
        val uid = userId ?: return
        db.collection("users").document(uid).collection("books")
            .document(updatedBook.id)
            .set(updatedBook)
            .addOnSuccessListener {
                fetchBooks()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Ошибка обновления книги: ${e.message}")
            }
    }

    fun refreshBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            fetchBooks()
            _isLoading.value = false
        }
    }
}
