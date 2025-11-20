package com.miempresa.ecocoinscampus.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 📱 Para dispositivo físico, usa tu IP local:
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // 📱 Para dispositivo físico, usa tu IP local:
    // private const val BASE_URL = "http://192.168.1.X:8080/"

    // 🌐 Para producción:
    // private const val BASE_URL = "https://api.ecocoinscampus.com/"

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

    // 🔥 ÚNICO CLIENTE RETROFIT PARA SPRING BOOT
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}