package com.example.bfu.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.example.bfu.R
import com.example.bfu.services.RegistrationService


class SplashFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_splash, container, false)
        val navController = NavHostFragment.findNavController(this)

        val progressBar = root.findViewById<View>(R.id.progressBar)
        val sharedPreferences = requireContext().getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val registrationService = RegistrationService(requireContext())

        //registrationService.logOut()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val isRegistered = registrationService.checkRegistration() //проверяем есть ли кто-то зарегистрированный
        val isLogged = registrationService.checkIfLogged() //вошёл?

        when {
            !isRegistered -> {
                navController.navigate(R.id.fragment_register)
            }
            !isLogged -> {
                navController.navigate(R.id.fragment_login)
            }
            else -> {
                navController.navigate(R.id.first_fragment)
            }
        }

        return root
    }

}