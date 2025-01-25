package com.example.bfu

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.bfu.services.RegistrationService

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var registrationService: RegistrationService

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        registrationService = RegistrationService(this)

        //registrationService.logOut()

        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val isRegistered = registrationService.checkRegistration() //проверяем есть ли кто-то зарегистрированный
        val isLogged = registrationService.checkIfLogged() //вошёл?

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
