package com.example.bullionapp.data.remote.response.update

import com.example.bullionapp.data.remote.response.addusers.RegisterDataResponse
import com.google.gson.annotations.SerializedName

data class DeleteResponse(
    //Field for success
    val status: Long? = null,
    val iserror: Boolean? = null,
    val message: String? = null,


    // Field for error
    @field:SerializedName("err_code")
    val errCode: String? = null,

    @field:SerializedName("err_message")
    val errMessage: String? = null,

    @field:SerializedName("err_message_en")
    val errMessageEn: String? = null
)