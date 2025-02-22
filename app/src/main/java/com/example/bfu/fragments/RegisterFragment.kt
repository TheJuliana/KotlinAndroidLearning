package com.example.bfu.fragments

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import com.example.bfu.ContentActivity
import com.example.bfu.R
import com.example.bfu.services.RegistrationService
import com.example.bfu.services.UIService
import com.example.bfu.services.ValidationService
import org.w3c.dom.Text

class RegisterFragment : Fragment() {
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_register, container, false)

        uiService = UIService(requireContext())
        validationService = ValidationService()
        registrationService = RegistrationService(requireContext())

        textByPhone = root.findViewById(R.id.textViewByPhone)
        textByEmail = root.findViewById(R.id.textViewByEmail)
        emailOrPhoneField = root.findViewById(R.id.editTextEmailOrPhone)
        password1Field = root.findViewById(R.id.editTextPassword)
        password2Field = root.findViewById(R.id.editTextPassword2)
        button = root.findViewById(R.id.button)

        setupListeners()

        return root
    }

    private fun setupListeners() {
        val navController = NavHostFragment.findNavController(this)
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
                navController.navigate(R.id.first_fragment)
            }
        }
    }

    private fun validateFields() : Triple<Boolean, String, String>  {
        val input = emailOrPhoneField.text.toString().trim()
        val password1 = password1Field.text.toString().trim()
        val password2 = password2Field.text.toString().trim()

        try {
            if (byPhone) {
                validationService.validatePhone(input, requireContext())
            } else {
                validationService.validateEmail(input, requireContext())
            }
            validationService.validatePasswords(password1, password2, requireContext())

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