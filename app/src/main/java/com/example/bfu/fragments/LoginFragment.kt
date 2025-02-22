package com.example.bfu.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.bfu.ContentActivity
import com.example.bfu.R
import com.example.bfu.services.RegistrationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var emailOrPhoneField: EditText
    private lateinit var passwordField: EditText
    private lateinit var checkBox: CheckBox
    private lateinit var notRegistered: TextView
    private lateinit var button: Button
    private var rememberMe = false
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var registrationService: RegistrationService

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        registrationService = RegistrationService(requireContext())

        emailOrPhoneField = root.findViewById(R.id.editTextEmailOrPhone)
        passwordField = root.findViewById(R.id.editTextPassword)
        checkBox = root.findViewById(R.id.checkBox)
        notRegistered = root.findViewById(R.id.not_registered)
        button = root.findViewById(R.id.button)

        auth = Firebase.auth

            setUpListeners()
            return root
        }

        private fun setUpListeners() {

            val navController = NavHostFragment.findNavController(this)

            notRegistered.setOnClickListener {  navController.navigate(R.id.fragment_register) }

            checkBox.setOnCheckedChangeListener { _, isChecked ->  rememberMe = isChecked}

            button.setOnClickListener {
                email = emailOrPhoneField.text.toString().trim()
                password = passwordField.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (rememberMe) {
                                registrationService.saveLoggedIn()
                            }
                            navController.navigate(R.id.first_fragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Ошибка входа: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

    }

