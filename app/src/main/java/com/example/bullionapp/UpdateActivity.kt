package com.example.bullionapp

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bullionapp.data.remote.request.UserUpdateRequest
import com.example.bullionapp.data.remote.response.home.UserItemResponse
import com.example.bullionapp.databinding.ActivityUpdateBinding
import com.example.bullionapp.di.Injection
import com.example.bullionapp.screen.home.HomeActivity
import com.example.bullionapp.screen.update.UpdateViewModel
import com.example.bullionapp.util.Utility
import com.example.bullionapp.util.Utility.animateVisibility
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var updateViewModel: UpdateViewModel
    private var dateOfBirthISO: String? = null

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateViewModel = UpdateViewModel(Injection.provideUserRepository(), Injection.provideAuthRepository(this))


        getIntentData()

        setErrorValidateForm()
        setButtonActions()
    }

    private fun getIntentData() {
        // Retrieve the User object from the intent
        val user: UserItemResponse? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SELECTED_USER", UserItemResponse::class.java)
        } else {
            intent.getParcelableExtra("SELECTED_USER")
        }

        user?.let {
            binding.apply {
                userId = it.id
                edtName.setText(it.name)
                edtEmail.setText(it.email)
                edtHomeAddress.setText(it.address)
                edtDateOfBirth.setText(Utility.dateTimeIsoToDateString(it.dateOfBirth))
                dateOfBirthISO = it.dateOfBirth
                edtPhoneNumber.setText(it.phone)

                if(it.gender == "male"){
                    cbItemMale.isChecked = true
                } else {
                    cbItemFemale.isChecked = true
                }
            }
        }
    }

    private fun setButtonActions() {
        binding.apply {
            lyDateOfBirth.setOnClickListener {
                startDatePicker { dateString ->
                    dateOfBirthISO = dateString
                    edtDateOfBirth.setText(Utility.convertIsoToDateString(dateString))
                    edtDateOfBirth.error = null
                }
            }

            icCalendar.setOnClickListener {
                startDatePicker { dateString ->
                    dateOfBirthISO = dateString
                    edtDateOfBirth.setText(Utility.convertIsoToDateString(dateString))
                    edtDateOfBirth.error = null
                }
            }

            btnUpdateUser.setOnClickListener {
                doUpdateUser()
            }

            btnDeleteUser.setOnClickListener {
                doDeleteUser()
            }


            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun doUpdateUser() {
        var isAllFieldValid = true

        binding.apply {
            if(edtName.text.isNullOrEmpty() || !edtName.error.isNullOrEmpty())
                isAllFieldValid = false

            if(!cbItemFemale.isChecked && !cbItemMale.isChecked)
                isAllFieldValid = false

            if(edtDateOfBirth.text.isNullOrEmpty() || dateOfBirthISO.isNullOrEmpty() || !edtDateOfBirth.error.isNullOrEmpty()) {
                isAllFieldValid = false
            }

            if(edtEmail.text.isNullOrEmpty() || !edtEmail.error.isNullOrEmpty())
                isAllFieldValid = false

            if(edtHomeAddress.text.isNullOrEmpty() || !edtHomeAddress.error.isNullOrEmpty())
                isAllFieldValid = false

            if(edtPhoneNumber.text.isNullOrEmpty() || !edtPhoneNumber.error.isNullOrEmpty())
                isAllFieldValid = false

        }

        if(isAllFieldValid) {
            binding.apply {
                val namesWords = edtName.text.toString().trim().split("\\s+".toRegex())
                val firstName = namesWords.first()
                val lastName = namesWords.drop(1).joinToString(" ")

                val gender = (if(cbItemFemale.isChecked) "female" else "male")
                val dateOfBirth = dateOfBirthISO.toString()
                val email = edtEmail.text.toString()
                val homeAddress = edtHomeAddress.text.toString()
                val phone = edtPhoneNumber.text.toString()
                val userRequest = UserUpdateRequest(
                    firstName = firstName,
                    lastName = lastName,
                    gender = gender,
                    dateOfBirth = dateOfBirth,
                    email = email,
                    phone = phone,
                    address = homeAddress,
                )

                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        showLoading(true)
                        updateViewModel.getAuthToken().collect {
                            updateViewModel.updateUser(
                                token = it.toString(),
                                id = userId ?: "",
                                user = userRequest
                            ).collect { response ->
                                response.onSuccess {
                                    Snackbar.make(
                                        binding.root,
                                        "Success update user",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    showLoading(false)
                                    navigateToHome()
                                }

                                response.onFailure {
                                    Snackbar.make(
                                        binding.root,
                                        "Failed update user",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    showLoading(false)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Snackbar.make(
                binding.root,
                "There is some field that empty or not valid, please check again your data",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun doDeleteUser(){
        binding.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    showLoading(true)
                    updateViewModel.getAuthToken().collect {
                        updateViewModel.deleteUser(
                            token = it.toString(),
                            id = userId ?: "",
                        ).collect { response ->
                            response.onSuccess {
                                Snackbar.make(
                                    binding.root,
                                    "Success delete user",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                showLoading(false)
                                navigateToHome()
                            }

                            response.onFailure {
                                Snackbar.make(
                                    binding.root,
                                    "Failed update user",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setErrorValidateForm() {
        binding.apply {
            //Set listener change for edit text name
            edtName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val regexNumber = Regex(".*\\d.*")
                    val words = s.toString().trim().split("\\s+".toRegex())
                    if (s.isNullOrEmpty() || s.length < 2 || regexNumber.containsMatchIn(s) || words.size < 2) {
                        edtName.error = "Invalid full name"
                    }
                }
            })

            // Handle eheck selection gender checkbox
            cbItemMale.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbItemFemale.isChecked = false
                }
            }

            cbItemFemale.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbItemMale.isChecked = false
                }
            }

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
                        edtEmail.error = "Invalid email address"
                    }
                }
            })

            // Set listener change for email edit text
            edtHomeAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        edtHomeAddress.error = "Invalid home address"
                    }
                }
            })

            //Set listener change for phone number text
            edtPhoneNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val regexPhone = "^08\\d{8,12}$".toRegex()
                    if (s.isNullOrEmpty() || !regexPhone.containsMatchIn(s)) {
                        edtPhoneNumber.error = "Invalid phone number"
                    }
                }
            })
        }
    }

    private fun startDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                val dateInIso = isoFormat.format(calendar.time)
                onDateSelected(dateInIso)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun navigateToHome(){
        startActivity(
            Intent(this@UpdateActivity, HomeActivity::class.java)
        )

        finish()
    }

    private fun showLoading(isLoading: Boolean){
        binding.apply {
            layoutLoading.root.animateVisibility(isLoading)
        }
    }
}