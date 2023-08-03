package com.paradoxo.gomugomunocompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.paradoxo.gomugomunocompose.ui.theme.GomuGomuNoComposeTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GomuGomuNoComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GomuGomuScreen()
                }
            }
        }
    }
}

@Composable
fun GomuGomuScreen() {

    var imageSize by remember { mutableStateOf(72.dp) }
    var currentGear by remember { mutableStateOf(0) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(offsetY.value) {
        Log.i("GomuGomuNoCompose", "offsetY.isRunning ${offsetY.isRunning}")

        if (offsetY.value.roundToInt() == 0 && !offsetY.isRunning) {
            currentGear += 1
        }

        if (offsetY.value.roundToInt() in 73..900) {
            if ((offsetY.value.roundToInt().dp / 2) > 72.dp) {
                imageSize = offsetY.value.roundToInt().dp / 2
            }
        }
    }

    val colorList =
        listOf(
            Color(0xFF250054),
            Color(0xFFE91E63),
            Color(0xFF9C27B0),
            Color(0xFF3F51B5),
            Color(0xFFFF9800),
        )

    val fruitColor = when (offsetY.value) {
        in -100f..1f -> colorList[0]
        in 1f..100f -> colorList[1]
        in 100f..400f -> colorList[2]
        in 400f..700f -> colorList[3]
        in 700f..1000f -> colorList[4]
        else -> colorList[4]

    }

    val color by animateColorAsState(
        targetValue = fruitColor,
        label = "",
        animationSpec = tween(
            durationMillis = 20,
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Current Gear: $currentGear")
        Text(text = "Offset: ${offsetY.value.roundToInt()}")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_gomu_gomu_color),
                contentDescription = null,
                colorFilter = ColorFilter.lighting(Color.White, color),
                modifier = Modifier
                    .size(imageSize)
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .pointerInput(Unit) {
                        forEachGesture {
                            awaitPointerEventScope {
                                //Detect a touch down event
                                awaitFirstDown()
                                do {
                                    val event: PointerEvent = awaitPointerEvent()
                                    event.changes.forEach { pointerInputChange: PointerInputChange ->
                                        //Consume the change
                                        scope.launch {
                                            offsetY.snapTo(
                                                offsetY.value + pointerInputChange.positionChange().y
                                            )
                                        }
                                    }
                                } while (event.changes.any { it.pressed })

                                // Touch released - Action_UP
                                scope.launch {
                                    offsetY.animateTo(
                                        targetValue = 0f, spring(
//                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                            dampingRatio = Spring.DampingRatioHighBouncy,
                                            stiffness = Spring.StiffnessLow

                                        )
                                    )
                                }
                            }
                        }
                    }
            )
        }


    }
}