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
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.CountDownViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography

class MainActivity : AppCompatActivity() {
    private val progressModel by viewModels<CountDownViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(progressModel)
            }
        }
    }
}

@Composable
fun MyApp(progressModel: CountDownViewModel? = null) {
    val timeValue = remember { mutableStateOf(10L) }
    val size = 300.dp
    Surface(color = MaterialTheme.colors.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.height(20.dp))
            Box(contentAlignment = Alignment.Center) {
                MyCircularProgressIndicator(
                    modifier = Modifier
                        .width(size)
                        .height(size),
                    progress = progressModel?.progressState?.value ?: 1f
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = progressModel?.minuteState?.value ?: "00:",
                        style = typography.h2,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = progressModel?.secondState?.value ?: "00:",
                        style = typography.h2,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = progressModel?.millisecondState?.value ?: "00",
                        style = typography.h2,
                        textAlign = TextAlign.Center
                    )
                }

            }
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    modifier = Modifier
                        .width(100.dp)
                        .height(60.dp)
                        .padding(2.dp),
                    placeholder = {
                        Text(text = stringResource(id = R.string.input_second))
                    },
                    value = "${timeValue.value}",
                    onValueChange = {
                        if (it.isNotEmpty()) {
                            try {
                                timeValue.value = it.toLong()
                            } catch (e: Exception) {
                                println(e.localizedMessage)
                                timeValue.value = 0
                            }
                        } else {
                            timeValue.value = 0
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.width(20.dp))
                Button(onClick = {
                    progressModel?.setMillisInFuture(1000 * timeValue.value)
                }) {
                    Text(text = "设置")
                }
            }
            MyButtons(isStart = progressModel?.isStartState?.value ?: false,
                onStart = {
                    progressModel?.isStartState?.value = true
                    progressModel?.onStart()
                },
                onCancel = {
                    progressModel?.isStartState?.value = false
                    progressModel?.onCancel()
                })
        }
    }
}

@Composable
fun MyButtons(
    isStart: Boolean, onStart: () -> Unit, onCancel: () -> Unit
) {
    Spacer(Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = if (!isStart) onStart else onCancel,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (!isStart) colorResource(
                    id = R.color.purple_500
                ) else Color.Red
            )
        ) {
            Text(text = if (!isStart) "开始" else "取消", color = Color.White)
        }
    }
}

@Composable
fun MyCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }
    Canvas(
        modifier
            .progressSemantics(progress)
            .size(40.dp)
            .focusable()
    ) {
        val startAngle = 270f
        val sweep = progress * 360f
        drawCircularIndicator(startAngle, sweep, color, stroke)
    }
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
