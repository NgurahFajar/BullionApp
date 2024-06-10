package com.example.bullionapp.screen.home

import com.example.bullionapp.data.remote.response.home.UserItemResponse
import com.example.bullionapp.databinding.ItemHomeBinding

interface UserItemCallback {
    fun onUserClicked(user: UserItemResponse, itemBinding: ItemHomeBinding)
}