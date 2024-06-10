package com.example.bullionapp.data.remote.response.update

import android.os.Parcelable
import com.example.bullionapp.data.remote.response.home.UserItemResponse
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class UpdateResponse (
    val status: Long,
    val iserror: Boolean,
    val message: String,
    val data: UserItemResponse,

    // Field for error
    @field:SerializedName("err_code")
    val errCode: String? = null,

    @field:SerializedName("err_message")
    val errMessage: String? = null,

    @field:SerializedName("err_message_en")
    val errMessageEn: String? = null
): Parcelable
