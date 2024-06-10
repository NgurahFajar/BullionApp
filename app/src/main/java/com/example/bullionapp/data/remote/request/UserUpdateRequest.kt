package com.example.bullionapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class UserUpdateRequest (
    @field:SerializedName("first_name")
    val firstName: String,

    @field:SerializedName("last_name")
    val lastName: String,

    val gender: String,

    @field:SerializedName("date_of_birth")
    val dateOfBirth: String,
    val email: String,
    val phone: String,
    val address: String
)