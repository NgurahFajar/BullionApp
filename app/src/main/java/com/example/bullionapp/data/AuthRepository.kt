package com.example.bullionapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.bullionapp.data.remote.config.ApiService
import com.example.bullionapp.data.remote.request.UserRequest
import com.example.bullionapp.data.remote.response.addusers.RegisterResponse
import com.example.bullionapp.data.remote.response.main.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody



class AuthRepository(private val apiService: ApiService,  private val dataStore: DataStore<Preferences>) {

    suspend fun addUser(
        email: RequestBody,
        password: RequestBody,
        firstName: RequestBody,
        lastName: RequestBody,
        gender: RequestBody,
        dateOfBirth: RequestBody,
        phone: RequestBody,
        address: RequestBody,
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
                address = address
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

    fun getAuthToken(): Flow<String?>{
        return dataStore.data.map {
            preferences -> preferences[TOKEN_KEY]
        }
    }

    suspend fun saveAuthToken(token:String){
        dataStore.edit {
            preferences -> preferences[TOKEN_KEY] = token
        }
    }

    suspend fun removeAuthToken(){
        dataStore.edit {
            preferences -> preferences.clear()
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token_key")
    }
}