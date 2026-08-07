package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.GlassSurface

@Composable
fun CyberGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorderCyan,
    backgroundColor: Color = GlassSurface,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(elevation, shape = shape, spotColor = borderColor, ambientColor = borderColor)
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(backgroundColor),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
