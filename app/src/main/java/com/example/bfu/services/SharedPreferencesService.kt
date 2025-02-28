package com.example.bfu.services

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SharedPreferencesService(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)

    fun saveRegistration() {
        sharedPreferences.edit()
            .putBoolean("isRegistered", true)
            .apply()
    }

    fun saveLoggedIn() {
        sharedPreferences.edit()
            .putBoolean("isLogged", true)
            .apply()
    }

    fun checkRegistration(): Boolean {
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false)
        return isRegistered
    }

    fun checkIfLogged(): Boolean {
        return sharedPreferences.getBoolean("isLogged", false)
    }

    fun logOut() {
        sharedPreferences.edit().clear().apply()
    }



}
