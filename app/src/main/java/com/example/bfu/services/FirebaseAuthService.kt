package com.example.bfu.services

import android.app.Activity
import androidx.navigation.fragment.NavHostFragment
import com.example.bfu.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class FirebaseAuthService(private val auth: FirebaseAuth) {

    fun sendVerificationCode(phoneNumber: String, activity: Activity, auth: FirebaseAuth, resendDelayMillis: Long, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks, timerService: TimerService) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(resendDelayMillis, TimeUnit.MILLISECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        timerService.start()
    }

    fun verifyCode(code: String, verificationId: String?): PhoneAuthCredential? {
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            return credential
        } else {
            return null
        }
    }
}