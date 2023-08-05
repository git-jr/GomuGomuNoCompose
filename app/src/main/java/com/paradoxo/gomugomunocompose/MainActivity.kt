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
import androidx.compose.foundation.background
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
                    color = MaterialTheme.colorScheme.surface
                ) {
                    var currentGear by remember { mutableStateOf(1) }
                    val activationGear = 5
                    val firstGear = 1
                    val lastGear = 5


                    // Make image blink
                    var imageAlphaDuration by remember { mutableStateOf(8000) }
                    val imageAlpha by remember { mutableStateOf(1.0f) }
                    var blink by remember { mutableStateOf(true) }
                    LaunchedEffect(currentGear == activationGear) {
                        while (true) {
                            if (imageAlphaDuration != 1000) {
                                imageAlphaDuration -= 500
                            }

                            blink = !blink
                            delay(1000)
                        }
                    }


                    // Change background color
                    val backgroundColor = remember { Animatable(Color.Transparent) }
                    LaunchedEffect(currentGear) {
                        when (currentGear) {
                            1 -> {
                                backgroundColor.animateTo(
                                    gearStageColor(0),
                                    animationSpec = tween(500),
                                )
                            }

                            2 -> {
                                backgroundColor.animateTo(
                                    gearStageColor(1),
                                    animationSpec = tween(500)
                                )
                            }

                            3 -> {
                                backgroundColor.animateTo(
                                    gearStageColor(2),
                                    animationSpec = tween(500)
                                )
                            }

                            4 -> {
                                backgroundColor.animateTo(
                                    gearStageColor(3),
                                    animationSpec = tween(500)
                                )

                            }

                            5 -> {
                                backgroundColor.animateTo(
                                    Color(0xFF261A44),
                                    animationSpec = tween(500)
                                )
                            }

                            else -> Brush.linearGradient(
                                listOf(Color(0xFF261A44), Color(0xFF261A44)),
                            )
                        }
                    }

                    Box(
                        Modifier
                            .background(backgroundColor.value.copy(alpha = 0.5f))
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedVisibility(
                            visible = (currentGear == activationGear) && blink,
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

    // Detect when the fruit was moved and changer your properties
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


    val fruitColor = when (offsetY.value) {
        in -100f..90f -> gearStageColor(0) // Normal
        in 91f..300f -> gearStageColor(1) // Gear 2
        in 301f..550f -> gearStageColor(2)  // Gear 3
        in 551f..850f -> gearStageColor(3)  // Gear 4
        in 851f..2000f -> gearStageColor(4)  // Gear 5
        else -> gearStageColor(0) // Normal
    }

    val animatedColorFilter by animateColorAsState(
        targetValue = fruitColor,
        label = "Change fruit color animation",
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
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_gomu_gomu_color_2),
                contentDescription = null,
                colorFilter = ColorFilter.lighting(Color.White, animatedColorFilter),
                modifier = Modifier
                    .size(imageSize)
                    .offset { IntOffset(0, currentFruitPosition) }
                    .pointerInput(Unit) {
                        // Detect a touch down event and start the drag
                        awaitEachGesture {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.changes.any { it.pressed }) {
                                    event.changes.forEach { pointerInputChange: PointerInputChange ->
                                        // Move the image by changes in the touch position
                                        scope.launch {
                                            offsetY.snapTo(
                                                offsetY.value + pointerInputChange.positionChange().y
                                            )
                                        }
                                    }
                                } else {
                                    // When the touch is released, animate the image back to its original position with a spring effect
                                    scope.launch {
                                        offsetY.animateTo(
                                            targetValue = 0f, spring(
                                                dampingRatio = Spring.DampingRatioHighBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                }
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
    val transitionDefaultDuration = 200

    Column(
        Modifier
            // interactionSource = MutableInteractionSource() and indication = null to remove ripple effect
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) { onChangeGear() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically(
                        animationSpec = tween(transitionDefaultDuration),
                        initialOffsetY = { it }
                    ) + fadeIn(tween(transitionDefaultDuration)) with
                            slideOutVertically(
                                targetOffsetY = { -it }
                            ) + fadeOut(tween(transitionDefaultDuration))
                } else {
                    slideInVertically(
                        animationSpec = tween(transitionDefaultDuration),
                        initialOffsetY = { -it }
                    ) + fadeIn() with slideOutVertically(
                        animationSpec = tween(transitionDefaultDuration),
                        targetOffsetY = { it }) + fadeOut(tween(transitionDefaultDuration))
                }.using(SizeTransform(clip = false))
            },
            label = "Change count gear animation"
        ) { countGear ->
            Text(
                text = "$countGear", fontSize = 200.sp,
                fontFamily = vinaSansFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(0.5f)
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }
        Spacer(Modifier.size(20.dp))
    }
}