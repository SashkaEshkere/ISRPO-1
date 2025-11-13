package com.example.librarymobile.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.BookWithVisitors
import com.example.librarymobile.repository.LibraryRepository
import kotlinx.coroutines.launch

class BooksViewModel(private val repo: LibraryRepository) : ViewModel() {

    val books = mutableStateListOf<BookWithVisitors>()
    var errorMessage: String? = null

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val list = repo.getBooksWithVisitors()
                books.clear()
                books.addAll(list)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun addBook(title: String, author: String?) {
        viewModelScope.launch {
            try {
                repo.createBook(title, author)
                refresh()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            try {
                repo.deleteBook(id)
                refresh()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun addLink(bookId: Int, visitorId: Int, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repo.addLink(bookId, visitorId)
                refresh()
                onDone(true, null)
            } catch (e: Exception) {
                onDone(false, e.message)
            }
        }
    }

    fun deleteLink(bookId: Int, visitorId: Int, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repo.deleteLink(bookId, visitorId)
                refresh()
                onDone(true, null)
            } catch (e: Exception) {
                onDone(false, e.message)
            }
        }
    }
}
