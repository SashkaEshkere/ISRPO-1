package com.example.librarymobile.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.Visitor
import com.example.librarymobile.repository.LibraryRepository
import kotlinx.coroutines.launch

class VisitorsViewModel(private val repo: LibraryRepository) : ViewModel() {

    val visitors = mutableStateListOf<Visitor>()
    var errorMessage: String? = null

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val list = repo.getVisitors()
                visitors.clear()
                visitors.addAll(list)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun addVisitor(name: String) {
        viewModelScope.launch {
            try {
                repo.createVisitor(name)
                refresh()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun deleteVisitor(id: Int) {
        viewModelScope.launch {
            try {
                repo.deleteVisitor(id)
                refresh()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}
