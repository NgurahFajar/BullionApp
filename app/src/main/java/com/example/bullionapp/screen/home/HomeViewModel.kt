package com.example.bullionapp.screen.home

import androidx.lifecycle.ViewModel
import com.example.bullionapp.data.AuthRepository
import com.example.bullionapp.data.UserRepository
import com.example.bullionapp.data.remote.response.home.UserListResponse
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,

):ViewModel() {

    fun getUserList(token: String): Flow<Result<UserListResponse>>{
//                return userRepository.getUserList(token = "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY1MDI4MGVmMDJlM2UxYmMwNjJiNjJiMyIsImlhdCI6MTcxNzkzNjkwNywiZXhwIjoxNzIwNTI4OTA3fQ.xPjfC_MFrzJxMWibunKkBndtTgSqvtjf1ZDKqnWhtww")
        return userRepository.getUserList(token = "Bearer " + token)
    }

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()
    suspend fun removeAuthToken() = authRepository.removeAuthToken()
}