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
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.bfu.ContentActivity
import com.example.bfu.R
import com.example.bfu.services.RegistrationService

class LoginFragment : Fragment() {
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

            val root = inflater.inflate(R.layout.fragment_login, container, false)
            registrationService = RegistrationService(requireContext())

            emailOrPhoneField = root.findViewById(R.id.editTextEmailOrPhone)
            passwordField = root.findViewById(R.id.editTextPassword)
            checkBox = root.findViewById(R.id.checkBox)
            button = root.findViewById(R.id.button)

            setUpListeners()
            return root
        }

        private fun setUpListeners() {

            val navController = NavHostFragment.findNavController(this)

            checkBox.setOnCheckedChangeListener { _, isChecked ->  rememberMe = isChecked}

            button.setOnClickListener {
                login = emailOrPhoneField.text.toString().trim()
                password = passwordField.text.toString().trim()

                val isRegistered = registrationService.checkIfUserRegistered(login, password)

                if (isRegistered) {
                    if (rememberMe) {
                        registrationService.saveLoggedIn()
                    }
                    navController.navigate(R.id.first_fragment)

                } else {
                    Toast.makeText(requireContext(), "Пользователь не существует", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

