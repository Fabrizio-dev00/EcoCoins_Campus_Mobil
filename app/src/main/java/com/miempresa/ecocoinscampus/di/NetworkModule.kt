package com.miempresa.ecocoinscampus.di

import com.miempresa.ecocoinscampus.data.remote.*
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
    fun provideDjangoApiService(): DjangoApiService {
        return RetrofitClient.djangoApi
    }

    @Provides
    @Singleton
    fun provideSpringApiService(): SpringApiService {
        return RetrofitClient.springApi
    }
}