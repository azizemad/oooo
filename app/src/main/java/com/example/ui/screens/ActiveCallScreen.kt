package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberGridBackground
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun ActiveCallScreen(viewModel: CyberViewModel) {
    val callState by viewModel.activeCallState.collectAsState()

    // Cyber Pulsing Ring for incoming call
    val infiniteTransition = rememberInfiniteTransition(label = "CallPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    CyberGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = if (callState.isIncoming && !callState.isConnected) "INCOMING CYBER CALL..." else if (callState.isConnected) "ENCRYPTED CALL IN PROGRESS" else "DIALING...",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Caller Avatar Frame with Pulsing Ring
                Box(
                    modifier = Modifier
                        .scale(if (callState.isIncoming && !callState.isConnected) pulseScale else 1.0f)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0x2200F0FF))
                        .border(2.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = callState.callerName.take(1).uppercase(),
                        color = NeonCyan,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = callState.callerName,
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = callState.phoneNumber,
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Call Timer
                if (callState.isConnected) {
                    val minutes = callState.durationSeconds / 60
                    val seconds = callState.durationSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        color = NeonGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Audio Wave Visualizer Canvas
            if (callState.isConnected) {
                CyberAudioVisualizer()
            }

            // In-Call Action Grid Controls
            if (callState.isConnected) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InCallControlButton(
                            icon = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            label = if (callState.isMuted) "Muted" else "Mute",
                            isActive = callState.isMuted,
                            onClick = { viewModel.toggleMute() }
                        )

                        InCallControlButton(
                            icon = Icons.Default.VolumeUp,
                            label = if (callState.isSpeakerOn) "Speaker On" else "Speaker",
                            isActive = callState.isSpeakerOn,
                            onClick = { viewModel.toggleSpeaker() }
                        )

                        InCallControlButton(
                            icon = Icons.Default.Bluetooth,
                            label = "Bluetooth",
                            isActive = callState.isBluetoothOn,
                            onClick = { viewModel.toggleBluetooth() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InCallControlButton(
                            icon = Icons.Default.Pause,
                            label = if (callState.isOnHold) "On Hold" else "Hold",
                            isActive = callState.isOnHold,
                            onClick = { viewModel.toggleHold() }
                        )

                        InCallControlButton(
                            icon = Icons.Default.FiberManualRecord,
                            label = if (callState.isRecording) "Recording" else "Record",
                            isActive = callState.isRecording,
                            activeColor = NeonRed,
                            onClick = { viewModel.toggleRecord() }
                        )

                        InCallControlButton(
                            icon = Icons.Default.Dialpad,
                            label = "Keypad",
                            isActive = false,
                            onClick = { }
                        )
                    }
                }
            }

            // Bottom Answer / End Call Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = if (callState.isIncoming && !callState.isConnected) Arrangement.SpaceEvenly else Arrangement.Center
            ) {
                if (callState.isIncoming && !callState.isConnected) {
                    // Answer Call
                    IconButton(
                        onClick = { viewModel.answerCall() },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Answer", tint = Color.Black, modifier = Modifier.size(36.dp))
                    }
                }

                // End Call
                IconButton(
                    onClick = { viewModel.endCall() },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(NeonRed)
                ) {
                    Icon(imageVector = Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
        }
    }
}

@Composable
fun InCallControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color = NeonCyan,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor.copy(alpha = 0.3f) else GlassSurface)
                .border(1.5.dp, if (isActive) activeColor else GlassBorderCyan, CircleShape)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = if (isActive) activeColor else TextPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = if (isActive) activeColor else TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun CyberAudioVisualizer() {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val width = size.width
        val height = size.height
        val barCount = 28
        val barWidth = width / barCount

        for (i in 0 until barCount) {
            val barHeight = (height * 0.2f + Math.sin((i + waveOffset).toDouble()).toFloat() * height * 0.35f).coerceAtLeast(10f)
            val x = i * barWidth
            val y = (height - barHeight) / 2

            drawRect(
                color = NeonCyan.copy(alpha = 0.7f),
                topLeft = Offset(x + 2f, y),
                size = androidx.compose.ui.geometry.Size(barWidth - 4f, barHeight)
            )
        }
    }
}
