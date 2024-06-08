package com.example.bullionapp.data.remote.response.addusers

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    //Field for success
    val status: Long? = null,
    val iserror: Boolean? = null,
    val message: String? = null,
    val data: RegisterDataResponse? = null,


    // Field for error
    @field:SerializedName("err_code")
    val errCode: String? = null,

    @field:SerializedName("err_message")
    val errMessage: String? = null,

    @field:SerializedName("err_message_en")
    val errMessageEn: String? = null
)

data class RegisterDataResponse(
    val name: String,
    val email: String
)