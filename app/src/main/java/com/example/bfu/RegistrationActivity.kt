package com.example.bfu

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bfu.services.UIService
import com.example.bfu.services.RegistrationService
import com.example.bfu.services.ValidationService

class RegistrationActivity : AppCompatActivity() {
    private var byPhone = true

    private lateinit var textByPhone: TextView
    private lateinit var textByEmail: TextView
    private lateinit var emailOrPhoneField: EditText
    private lateinit var password1Field: EditText
    private lateinit var password2Field: EditText
    private lateinit var button: Button

    private lateinit var uiService: UIService
    private lateinit var validationService: ValidationService
    private lateinit var registrationService: RegistrationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        uiService = UIService(this)
        validationService = ValidationService()
        registrationService = RegistrationService(this)

        textByPhone = findViewById(R.id.textViewByPhone)
        textByEmail = findViewById(R.id.textViewByEmail)
        emailOrPhoneField = findViewById(R.id.editTextEmailOrPhone)
        password1Field = findViewById(R.id.editTextPassword)
        password2Field = findViewById(R.id.editTextPassword2)
        button = findViewById(R.id.button)

        setupListeners()
    }

    private fun setupListeners() {
        emailOrPhoneField.setOnFocusChangeListener { _, hasFocus ->
            uiService.updateHint(emailOrPhoneField, byPhone, hasFocus)
        }

        textByPhone.setOnClickListener {
            switchInputMode(true)
        }

        textByEmail.setOnClickListener {
            switchInputMode(false)
        }

        button.setOnClickListener {
            val (isValid, input, password) = validateFields()

            if (isValid) {
                registrationService.saveRegistration(input, password)
                startActivity(Intent(this, ContentActivity::class.java))
                finish()
            }
        }
    }

    private fun validateFields() : Triple<Boolean, String, String>  {
        val input = emailOrPhoneField.text.toString().trim()
        val password1 = password1Field.text.toString().trim()
        val password2 = password2Field.text.toString().trim()

        try {
            if (byPhone) {
                validationService.validatePhone(input, this)
            } else {
                validationService.validateEmail(input, this)
            }
            validationService.validatePasswords(password1, password2, this)

            return Triple(true, input, password1)
        } catch (e: IllegalArgumentException) {
            when (e.message) {
                getString(R.string.error_enter_email), getString(R.string.error_invalid_email) -> emailOrPhoneField.error = e.message
                getString(R.string.error_enter_phone), getString(R.string.error_phone_format) -> emailOrPhoneField.error = e.message
                getString(R.string.error_empty_password), getString(R.string.error_short_password) -> password1Field.error = e.message
                getString(R.string.error_password_mismatch) -> password2Field.error = e.message
            }

            return Triple(false, input, password1)
        }
    }


    private fun switchInputMode(isPhone: Boolean) {
        byPhone = isPhone

        uiService.updateHint(emailOrPhoneField, byPhone, emailOrPhoneField.hasFocus())
        uiService.updateTextAppearance(textByPhone, isPhone)
        uiService.updateTextAppearance(textByEmail, !isPhone)

        switchInputType()
    }

    private fun switchInputType() {
        emailOrPhoneField.inputType = if (byPhone) {
            InputType.TYPE_CLASS_PHONE
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }
}
