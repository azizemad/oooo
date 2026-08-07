package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.db.entities.ContactEntity
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberGlassCard
import com.example.ui.theme.*

@Composable
fun DialPadScreen(viewModel: CyberViewModel) {
    val dialInput by viewModel.dialPadInput.collectAsState()
    val t9Matches by viewModel.t9Matches.collectAsState()
    val activeSimSlot by viewModel.activeSimSlot.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SIM Slot Selector Bar
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GlassSurface)
                .border(1.dp, GlassBorderCyan, RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (activeSimSlot == 1) NeonCyan.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable { viewModel.setActiveSimSlot(1) }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SIM 1 (CYBER)",
                    color = if (activeSimSlot == 1) NeonCyan else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (activeSimSlot == 2) NeonGreen.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable { viewModel.setActiveSimSlot(2) }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SIM 2 (SECURE)",
                    color = if (activeSimSlot == 2) NeonGreen else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Number Input Display with Copy/Paste/Clear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GlassSurface)
                .border(1.dp, GlassBorderCyan, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (dialInput.isEmpty()) "Enter Number..." else dialInput,
                color = if (dialInput.isEmpty()) TextMuted else TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )

            if (dialInput.isNotEmpty()) {
                IconButton(onClick = { viewModel.deleteDialPadDigit() }) {
                    Icon(imageVector = Icons.Default.Backspace, contentDescription = "Delete", tint = NeonCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // T9 Instant Contact Match List
        if (t9Matches.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorderCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                LazyColumn {
                    items(t9Matches) { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDialPadInput(contact.phoneNumber)
                                }
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = contact.name, color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = contact.phoneNumber, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // DialPad Keys
        val keypadButtons = listOf(
            listOf("1" to "", "2" to "ABC", "3" to "DEF"),
            listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
            listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
            listOf("*" to "", "0" to "+", "#" to "")
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in keypadButtons) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for ((digit, subtext) in row) {
                        DialButton(
                            digit = digit,
                            subtext = subtext,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.appendDialPadDigit(digit) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Call, SMS, WhatsApp Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SMS Quick Action
            IconButton(
                onClick = {
                    if (dialInput.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$dialInput"))
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Enter a phone number first", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0x2200F0FF))
                    .border(1.dp, NeonCyan, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Message, contentDescription = "SMS", tint = NeonCyan)
            }

            // Quick Call Main Button
            IconButton(
                onClick = {
                    if (dialInput.isNotEmpty()) {
                        viewModel.startCall("", dialInput)
                    } else {
                        Toast.makeText(context, "Enter a phone number to call", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(NeonGreen)
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = Color.Black, modifier = Modifier.size(32.dp))
            }

            // WhatsApp Quick Action
            IconButton(
                onClick = {
                    if (dialInput.isNotEmpty()) {
                        val cleanPhone = dialInput.replace(Regex("[^0-9]"), "")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone"))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Enter a phone number first", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0x2200FF66))
                    .border(1.dp, NeonGreen, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "WhatsApp", tint = NeonGreen)
            }
        }
    }
}

@Composable
fun DialButton(
    digit: String,
    subtext: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(CircleShape)
            .background(Color(0x1100F0FF))
            .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = digit,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            if (subtext.isNotEmpty()) {
                Text(
                    text = subtext,
                    color = NeonCyan.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
