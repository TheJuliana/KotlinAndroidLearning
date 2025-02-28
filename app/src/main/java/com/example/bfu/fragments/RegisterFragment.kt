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
import com.example.bfu.services.FirebaseAuthService
import com.example.bfu.services.SharedPreferencesService
import com.example.bfu.services.TimerService
import com.example.bfu.services.UIService
import com.example.bfu.services.ValidationService
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

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
    private lateinit var auth: FirebaseAuth

    private lateinit var uiService: UIService
    private lateinit var validationService: ValidationService
    private lateinit var sharedPreferencesService: SharedPreferencesService
    private lateinit var timerService: TimerService
    private lateinit var firebaseAuthService: FirebaseAuthService

    private val resendDelayMillis = 60000L // 60 секунд

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_register, container, false)

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

        uiService = UIService(requireContext())
        validationService = ValidationService()
        sharedPreferencesService = SharedPreferencesService(requireContext())
        timerService = TimerService(buttonSendCode,resendDelayMillis)
        firebaseAuthService = FirebaseAuthService(auth)

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
                    verifyCode(code)
                } else {
                    uiService.showToast("Введите код подтверждения")
                }
            } else {
                // регистрация по email
                val password = password1Field.text.toString().trim()
                val (isValid, _, _) = validationService.validateFields(emailOrPhoneField, password1Field, password2Field, requireContext(), byPhone)
                if (isValid) {
                    registerByEmail(input, password)
                }
            }
        }

        buttonSendCode.setOnClickListener {
            val phoneNumber = emailOrPhoneField.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                firebaseAuthService.sendVerificationCode(phoneNumber, requireActivity(), auth, resendDelayMillis, callbacks, timerService)  // Отправляем код
            } else {
                uiService.showToast("Введите номер телефона")
            }
        }
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
            // показываем поле для ввода кода и кнопку подтверждения
            editTextCode.visibility = View.VISIBLE
            buttonSendCode.isEnabled = false
            button.text = "Подтвердить код"
        }
    }

    private fun verifyCode(code: String) {
        val credential = firebaseAuthService.verifyCode(code, verificationId)
        if (credential != null) {
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun registerByEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sharedPreferencesService.saveRegistration()
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
                    // вход успешен
                    NavHostFragment.findNavController(this).navigate(R.id.first_fragment)
                } else {
                    uiService.showToast("Ошибка входа: ${task.exception?.message}")
                }
            }
    }

    private fun switchInputMode(isPhone: Boolean) {
        byPhone = isPhone

        if (isPhone) {
            // режим регистрации по номеру телефона
            password1Field.visibility = View.GONE
            password2Field.visibility = View.GONE
            editTextCode.visibility = View.VISIBLE
            buttonSendCode.visibility = View.VISIBLE
            button.text = "Подтвердить код"
        } else {
            // по email
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

    override fun onDestroyView() {
        super.onDestroyView()
        timerService.cancel()
    }


}