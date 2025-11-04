package com.dsm441.lecturadigital.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    //URL BASE
    private const val BASE_URL = "https://www.googleapis.com/"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    //Traemos los datos necesarios para retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    //Instancia de nuestro servicio
    val apiService: GoogleBooksApiService by lazy {
        retrofit.create(GoogleBooksApiService::class.java)
    }
}