package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberGlassCard
import com.example.ui.theme.*

@Composable
fun SettingsScreen(viewModel: CyberViewModel) {
    val currentTheme by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val pinLockEnabled by viewModel.preferences.pinLockEnabled.collectAsState()
    val pinCode by viewModel.preferences.pinCode.collectAsState()
    var showPinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "CYBER CONFIGURATION",
            color = NeonCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Engine
        Text(text = "THEME ENGINE", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(8.dp))

        CyberGlassCard(borderColor = GlassBorderCyan, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CyberThemeMode.values().forEach { mode ->
                    val isSelected = currentTheme == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) mode.primary.copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.dp, if (isSelected) mode.primary else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setThemeMode(mode) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(mode.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = mode.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = mode.primary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Font Engine & Language
        Text(text = "LANGUAGE & FONT ENGINE", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(8.dp))

        CyberGlassCard(borderColor = GlassBorderCyan, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "System Language", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (language == "EN") NeonCyan.copy(alpha = 0.3f) else GlassSurface)
                                .border(1.dp, if (language == "EN") NeonCyan else GlassBorderCyan, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setLanguage("EN") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "ENGLISH", color = if (language == "EN") NeonCyan else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (language == "AR") NeonGreen.copy(alpha = 0.3f) else GlassSurface)
                                .border(1.dp, if (language == "AR") NeonGreen else GlassBorderCyan, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setLanguage("AR") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "العربية", color = if (language == "AR") NeonGreen else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Divider(color = GlassBorderCyan.copy(alpha = 0.3f))

                // Font Live Preview
                Text(
                    text = if (language == "AR") "معاينة الخط العربي - السايبر ديلر بروفشنال" else "Live Font Preview - CyberDialer Pro",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Security Settings
        Text(text = "TERMINAL SECURITY", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(8.dp))

        CyberGlassCard(borderColor = GlassBorderCyan, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "PIN Lock Security", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Require 4-digit PIN on startup", color = TextMuted, fontSize = 11.sp)
                    }

                    Switch(
                        checked = pinLockEnabled,
                        onCheckedChange = { viewModel.setPinLockEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                if (pinLockEnabled) {
                    Divider(color = GlassBorderCyan.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPinDialog = true }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Change PIN Code (Current: ****)", color = NeonCyan, fontSize = 13.sp)
                        Icon(Icons.Default.Edit, contentDescription = null, tint = NeonCyan)
                    }
                }
            }
        }
    }

    if (showPinDialog) {
        ChangePinDialog(
            currentPin = pinCode,
            onDismiss = { showPinDialog = false },
            onUpdate = { newPin ->
                viewModel.setPinCode(newPin)
                showPinDialog = false
            }
        )
    }
}

@Composable
fun ChangePinDialog(
    currentPin: String,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var newPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("CHANGE SECURITY PIN", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = newPin,
                onValueChange = { if (it.length <= 4) newPin = it },
                label = { Text("New 4-Digit PIN") },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = GlassBorderCyan)
            )
        },
        confirmButton = {
            Button(
                onClick = { if (newPin.length == 4) onUpdate(newPin) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Text("UPDATE PIN", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextMuted) }
        },
        containerColor = DarkSurface
    )
}
