package com.ecocoins.campus.di

import android.content.Context
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.remote.*
import com.ecocoins.campus.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ========== LOCAL DATA ==========

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences {
        return UserPreferences(context)
    }

    // ========== RETROFIT SERVICES ==========

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }

    @Provides
    @Singleton
    fun provideRankingService(): RankingApiService {
        return RetrofitClient.rankingService
    }

    @Provides
    @Singleton
    fun provideLogrosService(): LogrosApiService {
        return RetrofitClient.logrosService
    }

    @Provides
    @Singleton
    fun provideEstadisticasService(): EstadisticasApiService {
        return RetrofitClient.estadisticasService
    }

    @Provides
    @Singleton
    fun provideNotificacionesService(): NotificacionesApiService {
        return RetrofitClient.notificacionesService
    }

    @Provides
    @Singleton
    fun provideReferidosService(): ReferidosApiService {
        return RetrofitClient.referidosService
    }

    @Provides
    @Singleton
    fun provideMapaService(): MapaApiService {
        return RetrofitClient.mapaService
    }

    @Provides
    @Singleton
    fun provideEducacionService(): EducacionApiService {
        return RetrofitClient.educacionService
    }

    @Provides
    @Singleton
    fun provideSoporteService(): SoporteApiService {
        return RetrofitClient.soporteService
    }

    // ========== REPOSITORIES ==========

    @Provides
    @Singleton
    fun provideAuthRepository(
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepository(userPreferences)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthRepository(
        userPreferences: UserPreferences
    ): FirebaseAuthRepository {
        return FirebaseAuthRepository(userPreferences)
    }

    @Provides
    @Singleton
    fun provideRankingRepository(): RankingRepository {
        return RankingRepository()
    }

    @Provides
    @Singleton
    fun provideLogrosRepository(): LogrosRepository {
        return LogrosRepository()
    }

    @Provides
    @Singleton
    fun provideEstadisticasRepository(): EstadisticasRepository {
        return EstadisticasRepository()
    }

    @Provides
    @Singleton
    fun provideNotificacionesRepository(): NotificacionesRepository {
        return NotificacionesRepository()
    }

    @Provides
    @Singleton
    fun provideReferidosRepository(): ReferidosRepository {
        return ReferidosRepository()
    }

    @Provides
    @Singleton
    fun provideMapaRepository(): MapaRepository {
        return MapaRepository()
    }

    @Provides
    @Singleton
    fun provideEducacionRepository(): EducacionRepository {
        return EducacionRepository()
    }

    @Provides
    @Singleton
    fun provideSoporteRepository(): SoporteRepository {
        return SoporteRepository()
    }

    @Provides
    @Singleton
    fun provideReciclajeRepository(): ReciclajeRepository {
        return ReciclajeRepository()
    }

    @Provides
    @Singleton
    fun provideRecompensasRepository(): RecompensasRepository {
        return RecompensasRepository()
    }
}