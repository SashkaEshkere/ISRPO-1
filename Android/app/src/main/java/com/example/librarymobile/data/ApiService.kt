package com.example.librarymobile.data

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/books")
    suspend fun getBooks(): Response<List<Book>>

    @GET("api/books/withvisitors")
    suspend fun getBooksWithVisitors(): Response<List<BookWithVisitors>>

    @POST("api/books")
    suspend fun createBook(@Body payload: Map<String, String?>): Response<Book>

    @DELETE("api/books/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Response<Unit>

    @GET("api/visitors")
    suspend fun getVisitors(): Response<List<Visitor>>

    @POST("api/visitors")
    suspend fun createVisitor(@Body payload: Map<String, String>): Response<Visitor>

    @DELETE("api/visitors/{id}")
    suspend fun deleteVisitor(@Path("id") id: Int): Response<Unit>

    @POST("api/bookvisitors")
    suspend fun addBookVisitor(@Body payload: Map<String, Int>): Response<Map<String, Boolean>>

    @POST("api/bookvisitors/delete")
    suspend fun deleteBookVisitor(@Body payload: Map<String, Int>): Response<Map<String, Boolean>>
}
