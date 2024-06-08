package com.example.bullionapp.data

import com.example.bullionapp.data.remote.config.ApiService
import com.example.bullionapp.data.remote.request.UserRequest
import com.example.bullionapp.data.remote.response.addusers.RegisterResponse
import com.example.bullionapp.data.remote.response.main.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody


class AuthRepository(private val apiService: ApiService) {

    suspend fun addUser(
        email: RequestBody,
        password: RequestBody,
        firstName: RequestBody,
        lastName: RequestBody,
        gender: RequestBody,
        dateOfBirth: RequestBody,
        phone: RequestBody,
        filePhoto: MultipartBody.Part
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.addUser(
                photo = filePhoto,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                dateOfBirth = dateOfBirth,
                phone = phone,
            )
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun userLogin(user: UserRequest): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(user = user)
            emit(Result.success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
}