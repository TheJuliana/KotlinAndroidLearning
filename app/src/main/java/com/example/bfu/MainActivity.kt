package com.example.bfu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isRegistered", false) // TODO(remove after develop)
        editor.apply()

        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val isRegistered = sharedPreferences.getBoolean("isRegistered", false)
        val isLogged = sharedPreferences.getBoolean("isLogged", false)

        when {
            !isRegistered -> {
                val intent = Intent(this, RegistrationActivity::class.java)
                startActivity(intent)
            }
            !isLogged -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                val intent = Intent(this, ContentActivity::class.java)
                startActivity(intent)
            }
        }

        finish()
    }
}
