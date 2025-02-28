package com.example.bfu.services

import android.os.CountDownTimer
import android.widget.Button

class TimerService(private val button: Button, private val delayMillis: Long) {
    private var countDownTimer: CountDownTimer? = null

    fun start() {
        button.isEnabled = false
        countDownTimer = object : CountDownTimer(delayMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                button.text = "Отправить повторно через ${millisUntilFinished / 1000} сек."
            }

            override fun onFinish() {
                button.text = "Отправить код"
                button.isEnabled = true
            }
        }.start()
    }

    fun cancel() {
        countDownTimer?.cancel()
    }
}