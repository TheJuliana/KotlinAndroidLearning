package com.example.bfu.services

import android.content.Context

class RegistrationService(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)

    fun saveRegistration(input: String, password: String) {
        sharedPreferences.edit()
            .putString("emailOrPhone", input)
            .putString("Password", password)
            .putBoolean("isRegistered", true)
            .apply()
    }

    fun saveLoggedIn() {
        sharedPreferences.edit()
            .putBoolean("isLogged", true)
            .apply()
    }

    fun checkIfUserRegistered(input: String, password: String): Boolean {

        val savedEmailOrPhone = sharedPreferences.getString("emailOrPhone", null)
        val savedPassword = sharedPreferences.getString("Password", null)

        return (savedEmailOrPhone == input && savedPassword == password)
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
