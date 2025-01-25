package com.example.bfu

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ContentActivity : AppCompatActivity() {
    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Логируем начало onCreate
        Log.d("AppActivity", "onCreate started")

        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false)

        // Убедитесь, что контент установлен
        setContentView(R.layout.activity_app)

        // Логируем, что контент был установлен
        Log.d("AppActivity", "Content view set to activity_main")

        text = findViewById(R.id.mainTextView)

        // Логируем текст, который мы устанавливаем
        Log.d("AppActivity", "Setting text for mainTextView: APP ACTIVITY: $isRegistered")

        text.text = buildString {
            append("APP ACTIVITY: ")
            append(isRegistered)
        }
    }
}
