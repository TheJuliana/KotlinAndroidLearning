package com.example.bfu.services

import android.content.Context
import android.provider.Settings.Global.getString
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
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

    fun validateFields(
        emailOrPhoneField: EditText,
        password1Field: EditText,
        password2Field: EditText,
        context: Context,
        byPhone: Boolean
    ): Triple<Boolean, String, String> {
        val input = emailOrPhoneField.text.toString().trim()
        val password1 = password1Field.text.toString().trim()
        val password2 = password2Field.text.toString().trim()

        try {
            if (byPhone) {
                this.validatePhone(input, context)
            } else {
                this.validateEmail(input, context)
            }
            this.validatePasswords(password1, password2, context)

            return Triple(true, input, password1)
        } catch (e: IllegalArgumentException) {
            when (e.message) {
                context.getString(R.string.error_enter_email), context.getString(R.string.error_invalid_email) -> emailOrPhoneField.error =
                    e.message

                context.getString(R.string.error_enter_phone), context.getString(R.string.error_phone_format) -> emailOrPhoneField.error =
                    e.message

                context.getString(R.string.error_empty_password), context.getString(R.string.error_short_password) -> password1Field.error =
                    e.message

                context.getString(R.string.error_password_mismatch) -> password2Field.error = e.message
            }

            return Triple(false, input, password1)
        }
    }
}
