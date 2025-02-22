package com.example.bfu.services

import android.content.Context

class RegistrationService(context: Context) {

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
