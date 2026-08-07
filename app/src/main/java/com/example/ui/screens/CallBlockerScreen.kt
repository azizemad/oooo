package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.db.entities.BlockedNumberEntity
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberGlassCard
import com.example.ui.theme.*

@Composable
fun CallBlockerScreen(viewModel: CyberViewModel) {
    val blockedNumbers by viewModel.blockedNumbers.collectAsState()
    var showAddBlockDialog by remember { mutableStateOf(false) }
    var blockUnknowns by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "CYBER SPAM SHIELD",
            color = NeonCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "AI-powered spam detection and automated call blocking blacklist database.",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Block Unknown Numbers Switch Card
        CyberGlassCard(
            borderColor = CallBlockedColor.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "BLOCK UNKNOWN NUMBERS", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Automatically silence callers not saved in your contacts.", color = TextMuted, fontSize = 11.sp)
                }
                Switch(
                    checked = blockUnknowns,
                    onCheckedChange = { blockUnknowns = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CallBlockedColor,
                        checkedTrackColor = CallBlockedColor.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CyberButton(
            text = "BLOCK NEW PHONE NUMBER",
            icon = Icons.Default.Block,
            accentColor = CallBlockedColor,
            onClick = { showAddBlockDialog = true },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Blacklist Database Header
        Text(
            text = "BLACKLIST DATABASE (${blockedNumbers.size})",
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (blockedNumbers.isEmpty()) {
            Text(text = "No blocked numbers in blacklist.", color = TextMuted, fontSize = 13.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(blockedNumbers) { blocked ->
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
                                Text(text = blocked.phoneNumber, color = CallBlockedColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Reason: ${blocked.reason}", color = TextSecondary, fontSize = 12.sp)
                            }

                            IconButton(onClick = { viewModel.unblockNumber(blocked) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Unblock", tint = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddBlockDialog) {
        AddBlockNumberDialog(
            onDismiss = { showAddBlockDialog = false },
            onBlock = { number, reason ->
                viewModel.blockNumber(number, reason)
                showAddBlockDialog = false
            }
        )
    }
}

@Composable
fun AddBlockNumberDialog(
    onDismiss: () -> Unit,
    onBlock: (number: String, reason: String) -> Unit
) {
    var number by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Known Robocaller / Spam") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("BLOCK PHONE NUMBER", color = CallBlockedColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Phone Number") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CallBlockedColor, unfocusedBorderColor = GlassBorderCyan)
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Blocking") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CallBlockedColor, unfocusedBorderColor = GlassBorderCyan)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (number.isNotEmpty()) {
                        onBlock(number, reason)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CallBlockedColor, contentColor = Color.Black)
            ) {
                Text("ADD TO BLACKLIST", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextMuted)
            }
        },
        containerColor = DarkSurface
    )
}
