package com.ecocoins.campus.di

import android.content.Context
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.data.remote.RetrofitClient
import com.ecocoins.campus.data.repository.*
import com.ecocoins.campus.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ============================================================================
    // NETWORKING
    // ============================================================================

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // ============================================================================
    // LOCAL DATA
    // ============================================================================

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences {
        return UserPreferences(context)
    }

    // ============================================================================
    // REPOSITORIES
    // ============================================================================

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepository(apiService, userPreferences)
    }

    @Provides
    @Singleton
    fun provideReciclajeRepository(
        apiService: ApiService,
        userPreferences: UserPreferences
    ): ReciclajeRepository {
        return ReciclajeRepository(apiService, userPreferences)
    }

    @Provides
    @Singleton
    fun provideRecompensasRepository(
        apiService: ApiService
    ): RecompensasRepository {
        return RecompensasRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideEstadisticasRepository(
        apiService: ApiService
    ): EstadisticasRepository {
        return EstadisticasRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideLogrosRepository(
        apiService: ApiService
    ): LogrosRepository {
        return LogrosRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideRankingRepository(
        apiService: ApiService
    ): RankingRepository {
        return RankingRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideNotificacionesRepository(
        apiService: ApiService
    ): NotificacionesRepository {
        return NotificacionesRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideMapaRepository(
        apiService: ApiService
    ): MapaRepository {
        return MapaRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideEducacionRepository(
        apiService: ApiService
    ): EducacionRepository {
        return EducacionRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideReferidosRepository(
        apiService: ApiService
    ): ReferidosRepository {
        return ReferidosRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideSoporteRepository(
        apiService: ApiService
    ): SoporteRepository {
        return SoporteRepository(apiService)
    }

    @Provides
    @Singleton
    fun providePerfilRepository(
        apiService: ApiService
    ): PerfilRepository {
        return PerfilRepository(apiService)
    }
}