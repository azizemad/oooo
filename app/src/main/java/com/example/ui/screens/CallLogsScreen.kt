package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.db.entities.CallLogEntity
import com.example.data.db.entities.CallType
import com.example.ui.CyberViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallLogsScreen(viewModel: CyberViewModel) {
    val callLogs by viewModel.callLogs.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    val filteredLogs = remember(callLogs, selectedFilter, searchQuery) {
        callLogs.filter { log ->
            val matchesType = when (selectedFilter) {
                "INCOMING" -> log.callType == CallType.INCOMING
                "OUTGOING" -> log.callType == CallType.OUTGOING
                "MISSED" -> log.callType == CallType.MISSED
                "BLOCKED" -> log.callType == CallType.BLOCKED
                else -> true
            }
            val matchesQuery = searchQuery.isEmpty() ||
                    log.callerName.contains(searchQuery, ignoreCase = true) ||
                    log.phoneNumber.contains(searchQuery)
            matchesType && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CALL LOG ARCHIVE",
                color = NeonCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            IconButton(onClick = { showClearDialog = true }) {
                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear", tint = NeonRed)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search logs...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = GlassBorderCyan,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Tabs Row
        val filters = listOf("ALL", "INCOMING", "OUTGOING", "MISSED", "BLOCKED")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else GlassSurface)
                        .border(1.dp, if (isSelected) NeonCyan else GlassBorderCyan, RoundedCornerShape(16.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) NeonCyan else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Logs List
        if (filteredLogs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No call records found.", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredLogs) { log ->
                    CallLogDetailCard(
                        log = log,
                        onCall = { viewModel.startCall(log.callerName, log.phoneNumber) },
                        onDelete = { viewModel.deleteCallLog(log) }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Call Logs?", color = NeonRed, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all call activity history from CyberDialer.", color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllCallLogs()
                    showClearDialog = false
                }) {
                    Text("CLEAR ALL", color = NeonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("CANCEL", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun CallLogDetailCard(
    log: CallLogEntity,
    onCall: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

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
                val (icon, color) = when (log.callType) {
                    CallType.INCOMING -> Icons.Default.CallReceived to CallIncomingColor
                    CallType.OUTGOING -> Icons.Default.CallMade to CallOutgoingColor
                    CallType.MISSED -> Icons.Default.CallMissed to CallMissedColor
                    CallType.BLOCKED -> Icons.Default.Block to CallBlockedColor
                }

                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (log.callerName.isNotEmpty()) log.callerName else log.phoneNumber,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = log.phoneNumber, color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = "${dateFormat.format(Date(log.timestamp))} • SIM ${log.simSlot} • ${log.durationSeconds}s",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Row {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                }
                IconButton(onClick = onCall) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = NeonGreen)
                }
            }
        }
    }
}
