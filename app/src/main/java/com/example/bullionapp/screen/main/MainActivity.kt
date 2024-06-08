package com.example.bullionapp.screen.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bullionapp.HomeActivity
import com.example.bullionapp.R
import com.example.bullionapp.data.remote.request.UserRequest
import com.example.bullionapp.databinding.ActivityMainBinding
import com.example.bullionapp.di.Injection
import com.example.bullionapp.screen.addusers.AddUsersActivity
import com.example.bullionapp.util.Utility
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = MainViewModel(Injection.provideAuthRepository())
        setButtonActions()
        setErrorValidateForm()

    }

    private fun setButtonActions(){
        binding.apply {
            btnAddNewUsers.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, AddUsersActivity::class.java)
                )
            }

            icVisibilityPassword.setOnClickListener {
                if(edtPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    icVisibilityPassword.setImageResource(R.drawable.ic_visible)
                } else {
                    edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    icVisibilityPassword.setImageResource(R.drawable.ic_visible_off)
                }
            }

            btnSignIn.setOnClickListener {
                doUserLogin()
            }
        }
    }

    private fun doUserLogin(){
        var isAllFieldValid = true

        binding.apply {
            if (edtEmail.text.isNullOrEmpty() || !edtEmail.error.isNullOrEmpty())
                isAllFieldValid = false
            if (edtPassword.text.isNullOrEmpty() || !edtPassword.error.isNullOrEmpty())
                isAllFieldValid = false

            if (isAllFieldValid){
                binding.apply {
                    val email = edtEmail.text.toString().trim()
                    val password = Utility.stringToSha256(edtPassword.text.toString()).trim()
                    val userRequest = UserRequest(email, password)

                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED){
                            mainViewModel.userLogin(
                                userRequest
                            ).collect {response ->
                                response.onSuccess {
                                    Snackbar.make(binding.root,
                                        "Success Login",
                                        Snackbar.LENGTH_LONG)
                                        .show()

                                    navigateToHome()
                                }

                                response.onFailure {
                                    Snackbar.make(binding.root,
                                        "Failed Login",Snackbar.LENGTH_LONG)
                                        .show()
                                }

                            }
                        }
                    }
                }
            }

            else {
                Snackbar.make(
                    binding.root,
                    "There is some field that empty or not valid, please check again your data",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun setErrorValidateForm(){
        binding.apply {
            // Set listener change for email edit text
            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                        edtEmail.error = "Must be email format"
                    }
                }
            })

            //Set listener change for password
            edtPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val regexPassword = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$".toRegex()
                    if (s.isNullOrEmpty() || !regexPassword.containsMatchIn(s)) {
                        edtPassword.error = "Minimal character 8"
                    }
                }
            })
        }
    }

    private fun navigateToHome(){
        startActivity(
            Intent(this@MainActivity, HomeActivity::class.java)
        )
    }

}