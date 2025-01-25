package com.example.bfu.services

import android.content.Context

class RegistrationService(private val context: Context) {
    fun saveRegistration(emailOrPhone: String, input: String, password: String) {
        val sharedPreferences = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(emailOrPhone, input)
            .putBoolean("isRegistered", true)
            .apply()
    }
}
