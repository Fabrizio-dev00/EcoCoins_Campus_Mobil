package com.ecocoins.campus.di

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.data.repository.*
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
}