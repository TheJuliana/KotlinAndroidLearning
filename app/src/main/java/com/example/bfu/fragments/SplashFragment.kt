package com.example.bfu.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.example.bfu.R
import com.example.bfu.services.SharedPreferencesService

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_splash, container, false)
        val navController = NavHostFragment.findNavController(this)

        val sharedPreferencesService = SharedPreferencesService(requireContext())

        //registrationService.logOut()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val isRegistered = sharedPreferencesService.checkRegistration() //проверяем есть ли кто-то зарегистрированный
        val isLogged = sharedPreferencesService.checkIfLogged() //вошёл?

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