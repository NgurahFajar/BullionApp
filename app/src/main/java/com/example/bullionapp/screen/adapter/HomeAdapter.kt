package com.example.bullionapp.screen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bullionapp.data.remote.response.home.UserItemResponse
import com.example.bullionapp.databinding.ItemHomeBinding
import com.example.bullionapp.screen.home.UserItemCallback
import com.example.bullionapp.util.Utility

class HomeAdapter (private val callback: UserItemCallback): ListAdapter<UserItemResponse, HomeAdapter.HomeViewHolder>(
    DiffCallback()
) {

    inner class HomeViewHolder(private val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserItemResponse) {
            binding.apply {
                root.setOnClickListener {
                    callback.onUserClicked(item, itemBinding = binding)
                }
                tvEmail.text = item.email
                tvName.text = item.name
                tvDate.text = Utility.dateTimeIsoToDateString(item.dateOfBirth, "MMM dd, yyyy")
                ivImage.setImageBitmap(Utility.base64ToBitmap(item.photo))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<UserItemResponse>() {
        override fun areItemsTheSame(oldItem: UserItemResponse, newItem: UserItemResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserItemResponse, newItem: UserItemResponse): Boolean {
            return oldItem == newItem
        }
    }
}