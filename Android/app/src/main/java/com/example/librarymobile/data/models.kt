package com.example.librarymobile.data

data class Book(
    val id: Int,
    val title: String,
    val author: String?
)

data class Visitor(
    val id: Int,
    val name: String
)

// Ответ для /api/books/withvisitors
data class BookWithVisitors(
    val id: Int,
    val title: String,
    val author: String?,
    val visitors: List<Visitor> = emptyList()
)
