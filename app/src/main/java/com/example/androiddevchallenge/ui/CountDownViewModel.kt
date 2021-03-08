package com.example.androiddevchallenge.ui

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class CountDownViewModel : ViewModel() {
    val progressState: MutableState<Float> = mutableStateOf(1F)
    val minuteState: MutableState<String> = mutableStateOf("00:")
    val secondState: MutableState<String> = mutableStateOf("00:")
    val millisecondState: MutableState<String> = mutableStateOf("00")
    private var millisInFuture = 60000L

    private val timer: CountDownTimer = object : CountDownTimer(millisInFuture, 100) {

        override fun onTick(millisUntilFinished: Long) {
            println("value=${millisUntilFinished.toFloat()}")
            progressState.value = millisUntilFinished.toFloat() / millisInFuture
            minuteState.value = String.format("%02d", millisUntilFinished / (1000 * 60)) + ":"
            secondState.value = String.format("%02d", millisUntilFinished / 1000) + ":"
            millisecondState.value = (millisUntilFinished % 1000 * 60).toString().substring(0,2)
        }

        override fun onFinish() {

        }
    }

    fun setMillisInFuture(millisInFuture: Long) {
        this.millisInFuture = millisInFuture
    }

    fun setTick(tick: Long) {
        timer.onTick(tick)
    }

    fun onStart() {
        timer.start()
    }

    fun onCancel() {
        timer.cancel()
    }

    override fun onCleared() {
        timer.cancel()
        super.onCleared()
    }

}