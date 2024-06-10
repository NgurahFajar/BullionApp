package com.example.bullionapp.data.remote.response.home

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class UserListResponse (

    //Field Success
    val status: Long?,
    val iserror: Boolean?,
    val message: String?,
    val data: List<UserItemResponse>,

    // Field for error
    @field:SerializedName("err_code")
    val errCode: String? = null,

    @field:SerializedName("err_message")
    val errMessage: String? = null,

    @field:SerializedName("err_message_en")
    val errMessageEn: String? = null
)

@Parcelize
data class UserItemResponse (
    @field:SerializedName("_id")
    val id: String,
    val name: String,
    val gender: String,

    @field:SerializedName("date_of_birth")
    val dateOfBirth: String,
    val email: String,
    var photo: String,
    val phone: String,
    val address: String?
): Parcelable