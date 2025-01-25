package com.example.bfu.services

import android.content.Context
import android.provider.Settings.Global.getString
import com.example.bfu.R

class ValidationService {
    fun validateEmail(email: String, context: Context): Boolean {
        if (email.isEmpty()) {
            throw IllegalArgumentException(context.getString(R.string.error_enter_email))
        }
        if (!email.contains("@")) {
            throw IllegalArgumentException(context.getString(R.string.error_invalid_email))
        }
        return true
    }

    fun validatePhone(phone: String, context: Context): Boolean {
        if (phone.isEmpty()) {
            throw IllegalArgumentException(context.getString(R.string.error_enter_phone))
        }
        if (!phone.startsWith("+")) {
            throw IllegalArgumentException(context.getString(R.string.error_phone_format))
        }
        return true
    }

    fun validatePasswords(password1: String, password2: String, context: Context): Boolean {
        if (password1.isEmpty()) {
            throw IllegalArgumentException(context.getString(R.string.error_empty_password))
        }
        if (password1.length < 8) {
            throw IllegalArgumentException(context.getString(R.string.error_short_password))
        }
        if (password2 != password1) {
            throw IllegalArgumentException(context.getString(R.string.error_password_mismatch))
        }
        return true
    }
}
