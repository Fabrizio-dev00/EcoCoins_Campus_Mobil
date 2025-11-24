package com.ecocoins.campus.di

import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.data.remote.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.api
    }
}