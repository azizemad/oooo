package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.CallLogEntity
import com.example.data.db.entities.ContactEntity
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberGlassCard
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: CyberViewModel,
    onNavigateTab: (Int) -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    val favorites by viewModel.favoriteContacts.collectAsState()
    val callLogs by viewModel.callLogs.collectAsState()
    val blockedNumbers by viewModel.blockedNumbers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Quick Cyber Stats Header
        CyberGlassCard(
            borderColor = GlassBorderCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CYBERDIALER PRO",
                        color = NeonCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Dev: Aziz Emad • Encrypted Terminal",
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2200FF66))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "ONLINE", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Grid Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Calls",
                value = "${callLogs.size}",
                icon = Icons.Default.Phone,
                accentColor = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Missed",
                value = "${callLogs.count { it.callType == com.example.data.db.entities.CallType.MISSED }}",
                icon = Icons.Default.CallMissed,
                accentColor = NeonRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Favorites",
                value = "${favorites.size}",
                icon = Icons.Default.Star,
                accentColor = NeonYellow,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Blocked",
                value = "${blockedNumbers.size}",
                icon = Icons.Default.Block,
                accentColor = CallBlockedColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Favorite Contacts Carousel
        Text(
            text = "QUICK FAVORITES",
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (favorites.isEmpty()) {
            Text(text = "No favorite contacts added yet.", color = TextMuted, fontSize = 13.sp)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites) { contact ->
                    FavoriteContactChip(contact = contact, onClick = {
                        viewModel.startCall(contact.name, contact.phoneNumber)
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Call Logs Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT CALL LOGS",
                color = NeonCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "SEE ALL",
                color = NeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateTab(2) } // Navigate to Call Logs
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (callLogs.isEmpty()) {
            Text(text = "No recent call activity.", color = TextMuted, fontSize = 13.sp)
        } else {
            callLogs.take(4).forEach { log ->
                RecentCallItem(log = log, onCall = {
                    viewModel.startCall(log.callerName, log.phoneNumber)
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    CyberGlassCard(
        borderColor = accentColor.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = title, color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun FavoriteContactChip(contact: ContactEntity, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorderCyan, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.2f))
                    .border(1.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = contact.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = contact.phoneNumber, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun RecentCallItem(log: CallLogEntity, onCall: () -> Unit) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (log.callType) {
                    com.example.data.db.entities.CallType.INCOMING -> Icons.Default.CallReceived
                    com.example.data.db.entities.CallType.OUTGOING -> Icons.Default.CallMade
                    com.example.data.db.entities.CallType.MISSED -> Icons.Default.CallMissed
                    com.example.data.db.entities.CallType.BLOCKED -> Icons.Default.Block
                }
                val color = when (log.callType) {
                    com.example.data.db.entities.CallType.INCOMING -> CallIncomingColor
                    com.example.data.db.entities.CallType.OUTGOING -> CallOutgoingColor
                    com.example.data.db.entities.CallType.MISSED -> CallMissedColor
                    com.example.data.db.entities.CallType.BLOCKED -> CallBlockedColor
                }

                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (log.callerName.isNotEmpty()) log.callerName else log.phoneNumber,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = log.phoneNumber, color = TextMuted, fontSize = 11.sp)
                }
            }

            IconButton(onClick = onCall) {
                Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = NeonGreen)
            }
        }
    }
}
