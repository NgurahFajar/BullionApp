package com.example.bullionapp.screen.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bullionapp.UpdateActivity
import com.example.bullionapp.data.remote.response.home.UserItemResponse
import com.example.bullionapp.databinding.ActivityHomeBinding
import com.example.bullionapp.databinding.ItemHomeBinding
import com.example.bullionapp.databinding.ItemHomeDetailBinding
import com.example.bullionapp.di.Injection
import com.example.bullionapp.screen.adapter.HomeAdapter
import com.example.bullionapp.screen.addusers.AddUsersActivity
import com.example.bullionapp.screen.main.MainActivity
import com.example.bullionapp.util.Utility
import com.example.bullionapp.util.Utility.animateVisibility
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = HomeViewModel(Injection.provideUserRepository(), Injection.provideAuthRepository(this))

        setupButtonActions()
        setupRecyclerView()
        loadUserList()

    }

    private fun setupButtonActions() {
        binding.btnAddNewUsers.setOnClickListener {
            startActivity(
                Intent(this, AddUsersActivity::class.java)
            )
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.removeAuthToken()

                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            homeAdapter = HomeAdapter(callback = object : UserItemCallback {
                override fun onUserClicked(user: UserItemResponse, itemBinding: ItemHomeBinding) {
                    showUpdateDialog(user)
                }
            })
            rvUser.layoutManager = LinearLayoutManager(this@HomeActivity)
            rvUser.adapter = homeAdapter
        }
    }
    private fun loadUserList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                showLoading(isLoading = true)
                homeViewModel.getAuthToken().collect {
                    homeViewModel.getUserList(token = it.toString())
                        .collect { response ->
                            response.onSuccess {
                                Snackbar.make(
                                    binding.root,
                                    "Success Load User",
                                    Snackbar.LENGTH_SHORT

                                ).show()
                                homeAdapter.submitList(it.data)
                                showLoading(isLoading = false)
                                binding.btnAddNewUsers.visibility = View.VISIBLE
                            }

                            response.onFailure {
                                    Snackbar.make(
                                        binding.root,
                                        "Failed get user list because ${it.localizedMessage}",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                showLoading(isLoading = false)
                            }
                        }
                }
            }
        }
    }

    private fun showUpdateDialog(item: UserItemResponse) {
        val dialogBinding = ItemHomeDetailBinding.inflate(layoutInflater)

        val dialog = Dialog(this@HomeActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.apply {
            tvAddress.text = item.address
            tvEmail.text = item.email
            tvGender.text = item.gender
            tvDateBirth.text = Utility.dateTimeIsoToDateString(item.dateOfBirth)
            tvPhoneNumber.text = item.phone
            ivImage.setImageBitmap(Utility.base64ToBitmap(item.photo))

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnEditUser.setOnClickListener {
                val intent = Intent(
                    this@HomeActivity, UpdateActivity::class.java
                ).apply {
                    putExtra("SELECTED_USER", item)
                }

                startActivity(intent)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showLoading(isLoading: Boolean){
        binding.apply {
            layoutLoading.root.animateVisibility(isLoading)
        }
    }

}