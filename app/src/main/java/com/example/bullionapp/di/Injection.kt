package com.example.bullionapp.di

import com.example.bullionapp.data.AuthRepository
import com.example.bullionapp.data.remote.config.ApiConfig
import com.example.bullionapp.data.remote.config.ApiService

object Injection {

    fun provideApiService(): ApiService = ApiConfig.getApiService()

    fun provideAuthRepository(): AuthRepository = AuthRepository(apiService = provideApiService())
}