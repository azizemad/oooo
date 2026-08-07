package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberGlassCard
import com.example.ui.theme.*

@Composable
fun AboutScreen(viewModel: CyberViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0x2200F0FF))
                .border(2.dp, NeonCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.cyber_dialer_logo_1785893798569),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CYBERDIALER PRO",
            color = NeonCyan,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = "Version 1.0.0 • Production Release",
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Developer Card
        CyberGlassCard(
            borderColor = NeonGreen.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "DEVELOPED BY", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Aziz Emad", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Senior Android Framework Architect", color = TextPrimary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Architecture Info Card
        CyberGlassCard(
            borderColor = GlassBorderCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "SYSTEM SPECIFICATIONS", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text(text = "• Architecture: MVVM + Clean Architecture", color = TextPrimary, fontSize = 12.sp)
                Text(text = "• UI Engine: Jetpack Compose + Material 3", color = TextPrimary, fontSize = 12.sp)
                Text(text = "• Database: Room Persistence Library", color = TextPrimary, fontSize = 12.sp)
                Text(text = "• Features: DialPad T9, Fake Call/Chat, Spam Blocker, Theme Engine", color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}
