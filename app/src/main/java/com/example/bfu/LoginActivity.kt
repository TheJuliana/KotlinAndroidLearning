package com.example.bfu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bfu.services.RegistrationService

class LoginActivity : AppCompatActivity() {

    private lateinit var emailOrPhoneField: EditText
    private lateinit var passwordField: EditText
    private lateinit var checkBox: CheckBox
    private lateinit var button: Button
    private var rememberMe = false
    private lateinit var login: String
    private lateinit var password: String

    private lateinit var registrationService: RegistrationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registrationService = RegistrationService(this)

        emailOrPhoneField = findViewById(R.id.editTextEmailOrPhone)
        passwordField = findViewById(R.id.editTextPassword)
        checkBox = findViewById(R.id.checkBox)
        button = findViewById(R.id.button)

        setUpListeners()
    }

    private fun setUpListeners() {

        checkBox.setOnCheckedChangeListener { _, isChecked ->  rememberMe = isChecked}

        button.setOnClickListener {
            login = emailOrPhoneField.text.toString().trim()
            password = passwordField.text.toString().trim()

            val isRegistered = registrationService.checkIfUserRegistered(login, password)

            if (isRegistered) {
                if (rememberMe) {
                    registrationService.saveLoggedIn()
                }
                startActivity(Intent(this, ContentActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Пользователь не существует", Toast.LENGTH_SHORT).show()
            }
        }
    }

}