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
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
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
    val isStart: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isPause: MutableState<Boolean> = remember { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    var temp: Float
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
            MyButtons(isStart = isStart.value, isPause = isPause.value,
                onStart = {
                    if (isPause.value) {
                        temp = progressModel?.progressState?.value ?: 1f
                        progressModel?.setMillisInFuture((1000 * 60 * temp).toLong())
                    }
                    isStart.value = true
                    isPause.value = false
                    progressModel?.onStart()
                },
                onCancel = {
                    isStart.value = false
                    progressModel?.onCancel()
                },
                onPause = {
                    isPause.value = true
                    temp = progressModel?.progressState?.value ?: 1f
                    progressModel?.let {
                        it.onCancel()
                    }
                },
                onSetting = {
                    if (showDialog) {
                        //SetTimeDialog(context = , onDismiss =  { showDialog = false })
                    }
                    progressModel?.setMillisInFuture(1000 * 60)
                })
        }
    }
}

@Composable
fun MyButtons(
    isStart: Boolean, isPause: Boolean, onStart: () -> Unit, onCancel: () -> Unit,
    onPause: () -> Unit, onSetting: () -> Unit
) {
    Spacer(Modifier.height(60.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = if (!isStart || isPause) onStart else onPause,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (!isStart || isPause) colorResource(
                    id = R.color.purple_500
                ) else Color.Red
            )
        ) {
            Text(text = if (!isStart || isPause) "开始" else "暂停")
        }
        Spacer(Modifier.width(20.dp))
        Button(onClick = if (!isStart) onSetting else onCancel) {
            Text(text = if (!isStart) "设置" else "取消")
        }
    }
}

@Composable
fun SetTimeDialog(context: CompositionContext, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "请设置时间(分钟)",
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
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
