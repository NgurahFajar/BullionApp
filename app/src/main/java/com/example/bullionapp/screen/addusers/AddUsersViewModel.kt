package com.example.bullionapp.screen.addusers

import androidx.lifecycle.ViewModel
import com.example.bullionapp.data.AuthRepository
import com.example.bullionapp.data.remote.response.addusers.RegisterResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddUsersViewModel(
    private val authRepository: AuthRepository
) : ViewModel(){
    suspend fun addUser(
        email: RequestBody,
        password: RequestBody,
        firstName: RequestBody,
        lastName: RequestBody,
        gender: RequestBody,
        dateOfBirth: RequestBody,
        phone: RequestBody,
        filePhoto: MultipartBody.Part
    ): Flow<Result<RegisterResponse>> = authRepository.addUser(email, password, firstName, lastName, gender, dateOfBirth, phone, filePhoto)

}