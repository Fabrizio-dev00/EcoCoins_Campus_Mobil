package com.miempresa.ecocoinscampus.di

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.remote.ApiService
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
        apiService: ApiService,
        userPreferences: UserPreferences
    ): RecompensasRepository {
        return RecompensasRepository(apiService, userPreferences)
    }

    @Provides
    @Singleton
    fun provideEstadisticasRepository(
        apiService: ApiService,
        userPreferences: UserPreferences
    ): EstadisticasRepository {
        return EstadisticasRepository(apiService, userPreferences)
    }
}