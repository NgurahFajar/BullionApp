package com.example.bullionapp.screen.addusers

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bullionapp.R
import com.example.bullionapp.databinding.ActivityAddUsersBinding
import com.example.bullionapp.di.Injection
import com.example.bullionapp.util.Utility
import com.example.bullionapp.util.Utility.animateVisibility
import com.example.bullionapp.util.Utility.convertIsoToDateString
import com.example.bullionapp.util.Utility.getFileNameFromUri
import com.example.bullionapp.util.Utility.reduceFileImage
import com.example.bullionapp.util.Utility.uriToFile
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Date
import java.util.Locale

class AddUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddUsersBinding
    private lateinit var addUserViewModel: AddUsersViewModel

    private var getFile: File? = null
    private var uriFile: Uri? = null
    private var dateOfBirthISO: String? = null

    private val launcherGalleryIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            uriFile = result.data?.data as Uri
            uriFile?.let {
                uriToFile(it, this).also { resultFile ->
                    getFile = resultFile
                }

                binding.edtPhotoProfile.setText(getFileNameFromUri(this@AddUsersActivity, it))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addUserViewModel = AddUsersViewModel(Injection.provideAuthRepository(this))
        setErrorValidateForm()
        setButtonActions()
    }

    private fun setButtonActions() {
        binding.apply {
            lyDateOfBirth.setOnClickListener {
                startDatePicker { dateString ->
                    dateOfBirthISO = dateString
                    edtDateOfBirth.setText(convertIsoToDateString(dateString))
                    edtDateOfBirth.error = null
                }
            }

            icInsertPhoto.setOnClickListener {
                startGalleryIntent()
            }

            btnAddNewUsers.setOnClickListener {
                doAddUser()
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

            icVisibilityPasswordConfirmation.setOnClickListener {
                if(edtPasswordConfirmation.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    edtPasswordConfirmation.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    icVisibilityPasswordConfirmation.setImageResource(R.drawable.ic_visible)
                } else {
                    edtPasswordConfirmation.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    icVisibilityPasswordConfirmation.setImageResource(R.drawable.ic_visible_off)
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun doAddUser() {
        var isAllFieldValid = true

        binding.apply {
            if (edtName.text.isNullOrEmpty() || !edtName.error.isNullOrEmpty())
                isAllFieldValid = false

            if (!cbItemFemale.isChecked && !cbItemMale.isChecked)
                isAllFieldValid = false

            if (edtDateOfBirth.text.isNullOrEmpty() || dateOfBirthISO.isNullOrEmpty() || !edtDateOfBirth.error.isNullOrEmpty()) {
                isAllFieldValid = false
            }

            if (edtEmail.text.isNullOrEmpty() || !edtEmail.error.isNullOrEmpty())
                isAllFieldValid = false

            if (edtPhoneNumber.text.isNullOrEmpty() || !edtPhoneNumber.error.isNullOrEmpty())
                isAllFieldValid = false

            if (getFile == null || uriFile == null || edtPhotoProfile.text.isNullOrEmpty() || !edtPhotoProfile.error.isNullOrEmpty()) {
                isAllFieldValid = false
            }

            if (edtPassword.text.isNullOrEmpty() || !edtPassword.error.isNullOrEmpty())
                isAllFieldValid = false

            if (edtPasswordConfirmation.text.isNullOrEmpty() || !edtPasswordConfirmation.error.isNullOrEmpty())
                isAllFieldValid = false
        }

        if (isAllFieldValid) {
            binding.apply {
                val textPlainMediaType = "text/plain".toMediaType()
                val file = reduceFileImage(getFile as File)
                val namesWords = edtName.text.toString().trim().split("\\s+".toRegex())

                val firstName = namesWords.first().toRequestBody(textPlainMediaType)
                val lastName =
                    namesWords.drop(1).joinToString(" ").toRequestBody(textPlainMediaType)
                val gender = (if (cbItemFemale.isChecked) "female" else "male").toRequestBody(
                    textPlainMediaType
                )
                val dateOfBirth = dateOfBirthISO.toString().toRequestBody(textPlainMediaType)
                val email = edtEmail.text.toString().toRequestBody(textPlainMediaType)
                val homeAddress = edtHomeAddress.text.toString().toRequestBody(textPlainMediaType)
                val phone = edtPhoneNumber.text.toString().toRequestBody(textPlainMediaType)
                val password = Utility.stringToSha256(edtPassword.text.toString())
                    .toRequestBody(textPlainMediaType)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        showLoading(true)
                        addUserViewModel.addUser(
                            email = email,
                            password = password,
                            firstName = firstName,
                            lastName = lastName,
                            gender = gender,
                            dateOfBirth = dateOfBirth,
                            phone = phone,
                            address = homeAddress,
                            filePhoto = imageMultipart,
                        ).collect { response ->
                            response.onSuccess {
                                Snackbar.make(
                                    binding.root,
                                    "Success add user",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                showLoading(false)
                            }

                            response.onFailure {
                                Snackbar.make(
                                    binding.root,
                                    "Failed add user",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                showLoading(false)
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

                // Set listener change for Home Address edit text
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
                        val regexPassword = "^(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*\\d).{8,}$".toRegex()
                        if (s.isNullOrEmpty() || !regexPassword.containsMatchIn(s)) {
                            edtPassword.error = "Invalid password"
                        }
                    }
                })

                edtPasswordConfirmation.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun afterTextChanged(s: Editable?) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (s.isNullOrEmpty() || s.toString() != edtPassword.text.toString()) {
                            edtPasswordConfirmation.error = "Password not match"
                        }
                    }
                })
            }
        }

    private fun startGalleryIntent(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Foto Profil")
        launcherGalleryIntent.launch(chooser)
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

    private fun showLoading(isLoading:Boolean){
        binding.apply {
            layoutLoading.root.animateVisibility(isLoading)
        }
    }

}