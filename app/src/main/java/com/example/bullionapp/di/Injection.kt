package com.example.bullionapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.bullionapp.data.AuthRepository
import com.example.bullionapp.data.UserRepository
import com.example.bullionapp.data.remote.config.ApiConfig
import com.example.bullionapp.data.remote.config.ApiService

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")
object Injection {

    private fun provideApiService(): ApiService = ApiConfig.getApiService()

    fun provideAuthRepository(context: Context): AuthRepository = AuthRepository(
        apiService = provideApiService(),
        dataStore = context.dataStore)

    fun provideUserRepository() : UserRepository = UserRepository(
        apiService = provideApiService()
    )
}