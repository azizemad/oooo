package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import kotlin.random.Random

@Composable
fun CyberGridBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonCyan,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CyberGrid")
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GridAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val gridSize = 60f

            // Draw cyber perspective grid lines
            var x = 0f
            while (x < width) {
                drawLine(
                    color = accentColor.copy(alpha = 0.05f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridSize
            }

            var y = gridOffset
            while (y < height) {
                drawLine(
                    color = accentColor.copy(alpha = 0.05f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridSize
            }

            // Top gradient fade
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.08f),
                        Color.Transparent,
                        AmoledBlack.copy(alpha = 0.7f)
                    )
                )
            )
        }

        content()
    }
}
