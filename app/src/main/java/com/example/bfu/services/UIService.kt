package com.example.bfu.services

import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bfu.R

class UIService(private val context: android.content.Context) {
    fun updateHint(field: EditText, isPhone: Boolean, hasFocus: Boolean) {
        field.hint = if (hasFocus) {
            if (isPhone) "+78005553535" else "email.example@gmail.com"
        } else {
            context.getString(if (isPhone) R.string.enter_phone else R.string.enter_email)
        }
    }

    fun updateTextAppearance(view: TextView, isActive: Boolean) {
        view.setTextColor(ContextCompat.getColor(context, if (isActive) R.color.text_active else android.R.color.black))
        view.paintFlags = if (isActive) android.graphics.Paint.UNDERLINE_TEXT_FLAG else 0
    }
}
