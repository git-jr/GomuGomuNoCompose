package com.paradoxo.gomugomunocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.paradoxo.gomugomunocompose.ui.theme.GomuGomuNoComposeTheme
import com.paradoxo.gomugomunocompose.ui.theme.vinaSansFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            GomuGomuNoComposeTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentGear by remember { mutableStateOf(1) }
                    val activationGear = 5
                    val firstGear = 1
                    val lastGear = 5

                    // to make image blink
                    var imageAlphaDuration by remember { mutableStateOf(8000) }
                    var pulsar by remember { mutableStateOf(true) }
                    val imageAlpha by remember { mutableStateOf(1.0f) }

                    LaunchedEffect(currentGear == activationGear) {
                        while (true) {
                            if (imageAlphaDuration != 1000) {
                                imageAlphaDuration -= 500
                            }

                            pulsar = !pulsar
                            delay(1000)
                        }
                    }

                    val backgroundColor = remember { Animatable(Color.Transparent) }

                    val backgroundLinearGradient = Brush.linearGradient(
                        listOf(Color(0xFF250054), backgroundColor.value),
                    )

                    when (currentGear) {
                        1 -> {
                            LaunchedEffect(Unit) {
                                backgroundColor.animateTo(
                                    Color.Red, animationSpec = tween(500),
                                )
                            }
                        }

                        2 -> {
                            LaunchedEffect(Unit) {
                                backgroundColor.animateTo(
                                    Color.Blue, animationSpec = tween(500)
                                )
                            }
                        }

                        3 -> {
                            LaunchedEffect(Unit) {
                                backgroundColor.animateTo(Color.Green, animationSpec = tween(500))
                            }
                        }

                        4 -> {
                            LaunchedEffect(Unit) {
                                backgroundColor.animateTo(Color.Yellow, animationSpec = tween(500))
                            }
                        }

                        5 -> {
                            LaunchedEffect(Unit) {
                                backgroundColor.animateTo(Color.Black, animationSpec = tween(500))
                            }
                        }

                        else -> Brush.linearGradient(
                            listOf(Color(0xFFFF8000), Color(0xFF3F51B5)),
                        )
                    }

                    Box(
                        Modifier
//                            .background(backgroundLinearGradient, alpha = 0.5f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        AnimatedVisibility(
                            visible = (currentGear == activationGear) && pulsar,
                            enter = fadeIn(
                                tween(
                                    durationMillis = 500,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                            exit = fadeOut(
                                tween(
                                    durationMillis = imageAlphaDuration,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wallpaper_luffy_moon),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize(),
                                alpha = imageAlpha
                            )
                        }


                        Box(
                            Modifier
//                            .background(backgroundLinearGradient, alpha = 0.5f)
                                .padding(top = 50.dp)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimateIncrementDecrementGear(
                                count = currentGear,
                                onChangeGear = {
                                    currentGear = if (currentGear == lastGear) {
                                        firstGear
                                    } else {
                                        currentGear + 1
                                    }
                                }
                            )

                            GomuGomuScreen(
                                onChangeGear = {
                                    currentGear = if (currentGear == lastGear) {
                                        firstGear
                                    } else {
                                        currentGear + 1
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GomuGomuScreen(
    onChangeGear: () -> Unit
) {

    var imageSize by remember { mutableStateOf(72.dp) }
    var currentGear by remember { mutableStateOf(0) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val currentFruitPosition = offsetY.value.roundToInt()

    var listLastTwentyValues by remember { mutableStateOf(listOf<Int>()) }

    LaunchedEffect(offsetY.value) {

        if (currentFruitPosition in 900..950 && !listLastTwentyValues.contains(currentFruitPosition)) {
            onChangeGear()
        }
        listLastTwentyValues = if (listLastTwentyValues.size == 20) {
            listOf()
        } else {
            listLastTwentyValues + currentFruitPosition
        }

        if (currentFruitPosition == 0 && !offsetY.isRunning) {
            currentGear += 1
        }

        if (currentFruitPosition in 73..1500) {
            if ((currentFruitPosition.dp / 2) > 72.dp) {
                imageSize = currentFruitPosition.dp / 2
            }
        }
    }

    val colorList =
        listOf(
            Color(0xFF250054), // Normal
            Color(0xFF900606), // Gear 2
            Color(0xFFD49518),// Gear 3
            Color(0xFF300101), // Gear 4
            Color(0xFFA2A2A3), // Gear 5
        )

    val fruitColor = when (offsetY.value) {
        in -100f..90f -> colorList[0] // Normal
        in 91f..300f -> colorList[1] // Gear 2
        in 301f..550f -> colorList[2] // Gear 3
        in 551f..850f -> colorList[3]  // Gear 4
        in 851f..2000f -> colorList[4]  // Gear 5
        else -> colorList[0]

    }

    val color by animateColorAsState(
        targetValue = fruitColor,
        label = "",
        animationSpec = tween(
            durationMillis = 500,
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Testando animações: $currentGear")
        Text(text = "Offset: $currentFruitPosition velelocity: ${offsetY.velocity}")


        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_gomu_gomu_color_2),
                contentDescription = null,
                colorFilter = ColorFilter.lighting(Color.White, color),
                modifier = Modifier
                    .size(imageSize)
                    .offset { IntOffset(0, currentFruitPosition) }
                    .pointerInput(Unit) {
                        //Detect a touch down event
                        awaitEachGesture {
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
            )
        }

    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimateIncrementDecrementGear(
    count: Int,
    onChangeGear: () -> Unit
) {
    val durationMillis = 200

    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically(
                        animationSpec = tween(durationMillis),
                        initialOffsetY = { it }
                    ) + fadeIn(tween(durationMillis)) with
                            slideOutVertically(
                                targetOffsetY = { -it }
                            ) + fadeOut(tween(durationMillis))
                } else {
                    slideInVertically(
                        animationSpec = tween(durationMillis),
                        initialOffsetY = { -it }
                    ) + fadeIn() with slideOutVertically(
                        animationSpec = tween(durationMillis),
                        targetOffsetY = { it }) + fadeOut(tween(durationMillis))
                }.using(SizeTransform(clip = false))
            },
            label = ""
        ) { countGear ->
            Text(
                text = "$countGear", fontSize = 200.sp,
                fontFamily = vinaSansFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(0.5f)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) { onChangeGear() }
            )
        }
        Spacer(Modifier.size(20.dp))
    }
}