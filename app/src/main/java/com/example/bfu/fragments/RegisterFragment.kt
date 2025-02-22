package com.example.bfu.fragments

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
import com.example.bfu.R
import com.example.bfu.services.RegistrationService
import com.example.bfu.services.UIService
import com.example.bfu.services.ValidationService
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

private const val s = "Введите код подтверждения"

class RegisterFragment : Fragment() {
    private var byPhone = true
    private var verificationId: String? = null

    private lateinit var textByPhone: TextView
    private lateinit var textByEmail: TextView
    private lateinit var emailOrPhoneField: EditText
    private lateinit var password1Field: EditText
    private lateinit var password2Field: EditText
    private lateinit var editTextCode: EditText
    private lateinit var buttonSendCode: Button
    private lateinit var buttonAlreadyRegistered: TextView
    private lateinit var button: Button

    private lateinit var uiService: UIService
    private lateinit var validationService: ValidationService
    private lateinit var registrationService: RegistrationService

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_register, container, false)

        uiService = UIService(requireContext())
        validationService = ValidationService()
        registrationService = RegistrationService(requireContext())

        textByPhone = root.findViewById(R.id.textViewByPhone)
        textByEmail = root.findViewById(R.id.textViewByEmail)
        emailOrPhoneField = root.findViewById(R.id.editTextEmailOrPhone)
        password1Field = root.findViewById(R.id.editTextPassword)
        password2Field = root.findViewById(R.id.editTextPassword2)
        editTextCode = root.findViewById(R.id.editTextCode)
        buttonSendCode = root.findViewById(R.id.buttonSendCode)
        buttonAlreadyRegistered = root.findViewById(R.id.button_already_registered)
        button = root.findViewById(R.id.button)

        auth = FirebaseAuth.getInstance()

        switchInputMode(byPhone)

        setupListeners()

        return root
    }

    private fun setupListeners() {
        val navController = NavHostFragment.findNavController(this)

        buttonAlreadyRegistered.setOnClickListener {
            navController.navigate(R.id.fragment_login)
        }

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
            val input = emailOrPhoneField.text.toString().trim()

            if (byPhone) {
                // регистрация по номеру телефона
                val code = editTextCode.text.toString().trim()
                if (code.isNotEmpty()) {
                    verifyCode(code)  // Проверяем код
                } else {
                    uiService.showToast("Введите код подтверждения")
                }
            } else {
                // Регистрация по email
                val password = password1Field.text.toString().trim()
                val (isValid, _, _) = validateFields()
                if (isValid) {
                    registerByEmail(input, password)
                }
            }
        }

        buttonSendCode.setOnClickListener {
            val phoneNumber = emailOrPhoneField.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(phoneNumber)  // Отправляем код
            } else {
                uiService.showToast("Введите номер телефона")
            }
        }
    }
    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            uiService.showToast("Ошибка верификации: ${e.message}")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            this@RegisterFragment.verificationId = verificationId
            uiService.showToast("Код отправлен")
            // Показываем поле для ввода кода и кнопку подтверждения
            editTextCode.visibility = View.VISIBLE
            buttonSendCode.visibility = View.GONE
            button.text = "Подтвердить код"
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun registerByEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    registrationService.saveRegistration()
                    NavHostFragment.findNavController(this).navigate(R.id.first_fragment)
                } else {
                    uiService.showToast("Ошибка регистрации: ${task.exception?.message}")
                }
            }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Вход успешен
                    NavHostFragment.findNavController(this).navigate(R.id.first_fragment)
                } else {
                    // Обработка ошибки
                    uiService.showToast("Ошибка входа: ${task.exception?.message}")
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

        if (isPhone) {
            // Режим регистрации по номеру телефона
            password1Field.visibility = View.GONE
            password2Field.visibility = View.GONE
            editTextCode.visibility = View.VISIBLE
            buttonSendCode.visibility = View.VISIBLE
            button.text = "Подтвердить код"
        } else {
            // Режим регистрации по email
            password1Field.visibility = View.VISIBLE
            password2Field.visibility = View.VISIBLE
            editTextCode.visibility = View.GONE
            buttonSendCode.visibility = View.GONE
            button.text = "Зарегистрироваться"
        }

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