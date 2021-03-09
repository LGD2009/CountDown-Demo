/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CountDownViewModel : ViewModel() {
    val progressState: MutableState<Float> = mutableStateOf(1F)
    val isStartState: MutableState<Boolean> = mutableStateOf(false)
    val minuteState: MutableState<String> = mutableStateOf("00:")
    val secondState: MutableState<String> = mutableStateOf("00:")
    val millisecondState: MutableState<String> = mutableStateOf("00")
    private var millisInFuture = 60000L

    private var timer: CountDownTimer = object : CountDownTimer(millisInFuture, 100) {

        override fun onTick(millisUntilFinished: Long) {
            println("value=${millisUntilFinished.toFloat()}")
            progressState.value = millisUntilFinished.toFloat() / millisInFuture
            val minute: Int = (millisUntilFinished / (1000 * 60)).toInt()
            val second: Int = ((millisUntilFinished - minute * 60 * 1000) / 1000).toInt()
            minuteState.value = String.format("%02d", minute) + ":"
            secondState.value = String.format("%02d", second) + ":"
            val s = (millisUntilFinished % 1000 * 60).toString()
            millisecondState.value = when {
                s.length > 2 -> {
                    s.substring(0, 2)
                }
                s.length < 2 -> {
                    s + "0"
                }
                else -> {
                    s
                }
            }
        }

        override fun onFinish() {
            millisecondState.value = "00"
            progressState.value = 0f
            isStartState.value = false
        }
    }

    fun setMillisInFuture(millisInFuture: Long) {
        timer.cancel()
        this.millisInFuture = millisInFuture
        progressState.value = 1f
        isStartState.value = false
        val minute: Int = (millisInFuture / (1000 * 60)).toInt()
        val second: Int = ((millisInFuture - minute * 60 * 1000) / 1000).toInt()
        minuteState.value = String.format("%02d", minute) + ":"
        secondState.value = String.format("%02d", second) + ":"
        millisecondState.value = "00"
    }

    fun setTick(tick: Long) {
        timer.onTick(tick)
    }

    fun onStart() {
        timer = object : CountDownTimer(millisInFuture, 100) {

            override fun onTick(millisUntilFinished: Long) {
                println("value=${millisUntilFinished.toFloat()}")
                progressState.value = millisUntilFinished.toFloat() / millisInFuture
                val minute: Int = (millisUntilFinished / (1000 * 60)).toInt()
                val second: Int = ((millisUntilFinished - minute * 60 * 1000) / 1000).toInt()
                minuteState.value = String.format("%02d", minute) + ":"
                secondState.value = String.format("%02d", second) + ":"
                val s = (millisUntilFinished % 1000 * 60).toString()
                millisecondState.value = when {
                    s.length > 2 -> {
                        s.substring(0, 2)
                    }
                    s.length < 2 -> {
                        s + "0"
                    }
                    else -> {
                        s
                    }
                }
            }

            override fun onFinish() {
                millisecondState.value = "00"
                progressState.value = 0f
                isStartState.value = false
            }
        }
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
