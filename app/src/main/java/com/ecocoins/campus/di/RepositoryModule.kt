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
    fun provideFirebaseAuthRepository(
        apiService: ApiService
    ): FirebaseAuthRepository {
        return FirebaseAuthRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository,
        userPreferences: UserPreferences,
        apiService: ApiService
    ): AuthRepository {
        return AuthRepository(firebaseAuthRepository, userPreferences, apiService)
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
    fun provideProfesorRepository(
        apiService: ApiService,
        userPreferences: UserPreferences
    ): ProfesorRepository {
        return ProfesorRepository(apiService, userPreferences)
    }
}