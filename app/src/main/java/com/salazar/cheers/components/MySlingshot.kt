package com.salazar.cheers.components

import androidx.compose.runtime.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Composable
fun rememberUpdatedSlingshot(
    offsetY: Float,
    maxOffsetY: Float,
    height: Int
): Slingshot {
    val offsetPercent = min(1f, offsetY / maxOffsetY)
    val adjustedPercent = max(offsetPercent - 0.4f, 0f) * 5 / 3
    val extraOffset = abs(offsetY) - maxOffsetY

    // Can accommodate custom start and slingshot distance here
    val slingshotDistance = maxOffsetY
    val tensionSlingshotPercent = max(
        0f, min(extraOffset, slingshotDistance * 2) / slingshotDistance
    )
    val tensionPercent = (
            (tensionSlingshotPercent / 4) -
                    (tensionSlingshotPercent / 4).pow(2)
            ) * 2
    val extraMove = slingshotDistance * tensionPercent * 2
    val targetY = height + ((slingshotDistance * offsetPercent) + extraMove).toInt()
    val offset = targetY - height
    val strokeStart = adjustedPercent * 0.8f

    val startTrim = 0f
    val endTrim = strokeStart.coerceAtMost(MaxProgressArc)

    val rotation = (-0.25f + 0.4f * adjustedPercent + tensionPercent * 2) * 0.5f
    val arrowScale = min(1f, adjustedPercent)

    return remember { Slingshot() }.apply {
        this.offset = offset
        this.startTrim = startTrim
        this.endTrim = endTrim
        this.rotation = rotation
        this.arrowScale = arrowScale
    }
}

@Stable
class Slingshot {
    var offset: Int by mutableStateOf(0)
    var startTrim: Float by mutableStateOf(0f)
    var endTrim: Float by mutableStateOf(0f)
    var rotation: Float by mutableStateOf(0f)
    var arrowScale: Float by mutableStateOf(0f)
}

internal const val MaxProgressArc = 0.8f
