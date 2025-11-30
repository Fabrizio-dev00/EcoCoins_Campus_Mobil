package com.ecocoins.campus.data.remote

import com.ecocoins.campus.utils.ApiConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(loggingInterceptor)
        .connectTimeout(ApiConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(ApiConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // ========== API SERVICES ==========

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val rankingService: RankingApiService by lazy {
        retrofit.create(RankingApiService::class.java)
    }

    val logrosService: LogrosApiService by lazy {
        retrofit.create(LogrosApiService::class.java)
    }

    val estadisticasService: EstadisticasApiService by lazy {
        retrofit.create(EstadisticasApiService::class.java)
    }

    val notificacionesService: NotificacionesApiService by lazy {
        retrofit.create(NotificacionesApiService::class.java)
    }

    val referidosService: ReferidosApiService by lazy {
        retrofit.create(ReferidosApiService::class.java)
    }

    val mapaService: MapaApiService by lazy {
        retrofit.create(MapaApiService::class.java)
    }

    val educacionService: EducacionApiService by lazy {
        retrofit.create(EducacionApiService::class.java)
    }

    val soporteService: SoporteApiService by lazy {
        retrofit.create(SoporteApiService::class.java)
    }
}