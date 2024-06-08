package com.example.bullionapp.data.remote.response.main

data class LoginResponse(
    val status: Long? = null,
    val iserror: String,
    val message: String,
    val data: LoginDataResponse? = null

)

data class LoginDataResponse(
    val name: String,
    val email: String,
    val token: String
)