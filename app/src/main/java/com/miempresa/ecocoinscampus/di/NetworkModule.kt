package com.miempresa.ecocoinscampus.di

import com.miempresa.ecocoinscampus.data.remote.ApiService
import com.miempresa.ecocoinscampus.data.remote.RetrofitClient
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