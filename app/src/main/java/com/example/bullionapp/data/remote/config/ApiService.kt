package com.example.bullionapp.data.remote.config

import com.example.bullionapp.data.remote.request.UserRequest
import com.example.bullionapp.data.remote.response.addusers.RegisterResponse
import com.example.bullionapp.data.remote.response.main.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/api/v1/auth/register")
    suspend fun addUser(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("date_of_birth") dateOfBirth: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part photo: MultipartBody.Part,
        @Part("password") password: RequestBody?
    ): RegisterResponse

    @FormUrlEncoded
    @POST("/api/v1/auth/login")
    suspend fun userLogin (
        @Body user:UserRequest
    ):LoginResponse
}