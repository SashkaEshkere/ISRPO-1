package com.example.librarymobile.repository

import com.example.librarymobile.data.*
import java.io.IOException

class LibraryRepository(private val api: ApiService) {

    suspend fun getBooksWithVisitors(): List<BookWithVisitors> {
        val resp = api.getBooksWithVisitors()
        if (resp.isSuccessful) return resp.body() ?: emptyList()
        throw IOException("Error getting books: ${resp.code()} ${resp.message()}")
    }

    suspend fun createBook(title: String, author: String?) : Book {
        val body = mapOf("title" to title, "author" to author)
        val resp = api.createBook(body)
        if (resp.isSuccessful) return resp.body()!!
        throw IOException("Error creating book: ${resp.code()}")
    }

    suspend fun deleteBook(id: Int) {
        val resp = api.deleteBook(id)
        if (!resp.isSuccessful) throw IOException("Delete book failed: ${resp.code()}")
    }

    suspend fun getVisitors(): List<Visitor> {
        val resp = api.getVisitors()
        if (resp.isSuccessful) return resp.body() ?: emptyList()
        throw IOException("Error getting visitors: ${resp.code()}")
    }

    suspend fun createVisitor(name: String): Visitor {
        val body = mapOf("name" to name)
        val resp = api.createVisitor(body)
        if (resp.isSuccessful) return resp.body()!!
        throw IOException("Error creating visitor: ${resp.code()}")
    }

    suspend fun deleteVisitor(id: Int) {
        val resp = api.deleteVisitor(id)
        if (!resp.isSuccessful) throw IOException("Delete visitor failed: ${resp.code()}")
    }

    suspend fun addLink(bookId: Int, visitorId: Int) {
        val body = mapOf("bookId" to bookId, "visitorId" to visitorId)
        val resp = api.addBookVisitor(body)
        if (!resp.isSuccessful) throw IOException("Add link failed: ${resp.code()}")
    }

    suspend fun deleteLink(bookId: Int, visitorId: Int) {
        val body = mapOf("bookId" to bookId, "visitorId" to visitorId)
        val resp = api.deleteBookVisitor(body)
        if (!resp.isSuccessful) throw IOException("Delete link failed: ${resp.code()}")
    }
}
