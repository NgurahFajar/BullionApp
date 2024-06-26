package com.example.bullionapp.screen.update

import androidx.lifecycle.ViewModel
import com.example.bullionapp.data.AuthRepository
import com.example.bullionapp.data.UserRepository
import com.example.bullionapp.data.remote.request.UserUpdateRequest
import com.example.bullionapp.data.remote.response.update.DeleteResponse
import com.example.bullionapp.data.remote.response.update.UpdateResponse
import kotlinx.coroutines.flow.Flow

class UpdateViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel(){

    fun updateUser(
        token: String,
        id: String,
        user: UserUpdateRequest
    ): Flow<Result<UpdateResponse>>
            = userRepository.updateUser("Bearer " + token, id, user)

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun deleteUser(
        token: String,
        id: String,
    ): Flow<Result<DeleteResponse>> = userRepository.deleteUser("Bearer " + token, id)
}