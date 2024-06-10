package com.example.bullionapp.data

import com.example.bullionapp.data.remote.config.ApiService
import com.example.bullionapp.data.remote.request.UserUpdateRequest
import com.example.bullionapp.data.remote.response.home.UserListResponse
import com.example.bullionapp.data.remote.response.update.DeleteResponse
import com.example.bullionapp.data.remote.response.update.UpdateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository(private val apiService: ApiService) {

    fun getUserList(
        token: String,
        offset: Int? = null,
        limit: Int? = null,
    ): Flow<Result<UserListResponse>> = flow{
        try {
            val response = apiService.getUserList(token, offset, limit)
            emit(Result.success(response))
        } catch (e:Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    fun updateUser(
        token: String,
        id: String,
        user: UserUpdateRequest
    ): Flow<Result<UpdateResponse>> = flow {
        try {
            val response = apiService.updateUser(token, id, user)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    fun deleteUser(
        token: String,
        id: String,
    ): Flow<Result<DeleteResponse>> = flow {
        try {
            val response = apiService.deleteUser(token, id)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
}