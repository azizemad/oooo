package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberGlassCard
import com.example.ui.theme.*

@Composable
fun FakeCallScreen(viewModel: CyberViewModel) {
    var callerName by remember { mutableStateOf("Matrix HQ") }
    var phoneNumber by remember { mutableStateOf("+1 888-999-0000") }
    var selectedDelay by remember { mutableIntStateOf(10) } // Seconds
    val fakeCalls by viewModel.fakeCalls.collectAsState()
    val context = LocalContext.current

    val delayOptions = listOf(
        "5 sec" to 5,
        "10 sec" to 10,
        "30 sec" to 30,
        "1 min" to 60,
        "5 min" to 300
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "FAKE CALL SIMULATOR",
            color = NeonCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Schedule realistic fake incoming call simulation with full interactive screen and answer capabilities.",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        CyberGlassCard(
            borderColor = GlassBorderCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = callerName,
                    onValueChange = { callerName = it },
                    label = { Text("Caller Name", color = NeonCyan) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorderCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number", color = NeonCyan) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeonCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorderCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "SCHEDULE TIMER DELAY",
                    color = NeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    delayOptions.forEach { (label, sec) ->
                        val isSelected = selectedDelay == sec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonGreen.copy(alpha = 0.25f) else GlassSurface)
                                .border(1.dp, if (isSelected) NeonGreen else GlassBorderCyan, RoundedCornerShape(10.dp))
                                .clickable { selectedDelay = sec }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) NeonGreen else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                CyberButton(
                    text = "TRIGGER FAKE CALL NOW",
                    icon = Icons.Default.Call,
                    accentColor = NeonGreen,
                    onClick = {
                        viewModel.scheduleFakeCall(callerName, phoneNumber, selectedDelay)
                        Toast.makeText(context, "Fake call scheduled in $selectedDelay seconds!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Scheduled Fake Calls History
        Text(
            text = "FAKE CALL HISTORY",
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (fakeCalls.isEmpty()) {
            Text(text = "No scheduled fake calls history.", color = TextMuted, fontSize = 13.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                fakeCalls.forEach { fakeCall ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorderCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = fakeCall.callerName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = fakeCall.phoneNumber, color = TextSecondary, fontSize = 12.sp)
                                Text(text = "Delay: ${fakeCall.delaySeconds}s • Ringtone: ${fakeCall.ringtoneName}", color = TextMuted, fontSize = 10.sp)
                            }

                            IconButton(onClick = { viewModel.startCall(fakeCall.callerName, fakeCall.phoneNumber, isIncoming = true) }) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Test", tint = NeonCyan)
                            }
                        }
                    }
                }
            }
        }
    }
}
