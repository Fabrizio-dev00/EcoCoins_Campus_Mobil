package com.miempresa.ecocoinscampus.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URLs de tus backends
    private const val DJANGO_BASE_URL = "http://10.0.2.2:8000/api/"
    private const val SPRING_BASE_URL = "http://10.0.2.2:8080/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Retrofit para Django (Auth + Materiales)
    val djangoApi: DjangoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DJANGO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(DjangoApiService::class.java)
    }

    // Retrofit para Spring Boot (Estadísticas)
    val springApi: SpringApiService by lazy {
        Retrofit.Builder()
            .baseUrl(SPRING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SpringApiService::class.java)
    }
}