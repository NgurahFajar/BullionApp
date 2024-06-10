package com.example.bullionapp.data.remote.response.main

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    // field for success
    val status: Long? = null,
    val iserror: String,
    val message: String,
    val data: LoginDataResponse? = null,

    //field for error
    @field:SerializedName("err_code")
    val errCode: String? = null,

    @field:SerializedName("err_message")
    val errMessage: String? = null,

    @field:SerializedName("err_message_en")
    val errMessageEn: String? = null

)

data class LoginDataResponse(
    val name: String,
    val email: String,
    val token: String
)