package com.miempresa.ecocoinscampus.di

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.remote.*
import com.miempresa.ecocoinscampus.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        djangoApi: DjangoApiService,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepository(djangoApi, userPreferences)
    }

    @Provides
    @Singleton
    fun provideMaterialRepository(
        djangoApi: DjangoApiService,
        userPreferences: UserPreferences
    ): MaterialRepository {
        return MaterialRepository(djangoApi, userPreferences)
    }

    @Provides
    @Singleton
    fun provideEstadisticasRepository(
        springApi: SpringApiService,
        userPreferences: UserPreferences
    ): EstadisticasRepository {
        return EstadisticasRepository(springApi, userPreferences)
    }

    @Provides
    @Singleton
    fun provideRecompensasRepository(
        djangoApi: DjangoApiService,
        userPreferences: UserPreferences
    ): RecompensasRepository {
        return RecompensasRepository(djangoApi, userPreferences)
    }
}