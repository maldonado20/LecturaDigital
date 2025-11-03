package com.dsm441.lecturadigital.network

import com.dsm441.lecturadigital.data.GoogleBookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {

    //Busca libro por titulo+autor y trae el resueltado
    @GET("books/v1/volumes")
    suspend fun searchBook(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 1
    ): Response<GoogleBookResponse>
}