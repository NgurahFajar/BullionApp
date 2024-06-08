package com.example.bullionapp.screen.main

import androidx.lifecycle.ViewModel
import com.example.bullionapp.data.AuthRepository
import com.example.bullionapp.data.remote.request.UserRequest
import com.example.bullionapp.data.remote.response.main.LoginResponse
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val authRepository: AuthRepository):ViewModel() {
    suspend fun userLogin(user: UserRequest): Flow<Result<LoginResponse>> = authRepository.userLogin(user)
}