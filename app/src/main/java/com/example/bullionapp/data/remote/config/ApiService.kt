package com.example.bullionapp.data.remote.config

import com.example.bullionapp.data.remote.request.UserRequest
import com.example.bullionapp.data.remote.request.UserUpdateRequest
import com.example.bullionapp.data.remote.response.addusers.RegisterResponse
import com.example.bullionapp.data.remote.response.home.UserListResponse
import com.example.bullionapp.data.remote.response.main.LoginResponse
import com.example.bullionapp.data.remote.response.update.DeleteResponse
import com.example.bullionapp.data.remote.response.update.UpdateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
        @Part("address") address: RequestBody?,
        @Part photo: MultipartBody.Part,
        @Part("password") password: RequestBody?
    ): RegisterResponse



    @POST("/api/v1/auth/login")
    suspend fun userLogin (
        @Body user:UserRequest
    ):LoginResponse

    @GET("/api/v1/admin")
    suspend fun getUserList(
        @Header("Authorization") token: String,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null
    ): UserListResponse

    @PUT("/api/v1/admin/{id}/update")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body user: UserUpdateRequest
    ): UpdateResponse

    @DELETE("/api/v1/admin/{id}/delete")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ):DeleteResponse

}