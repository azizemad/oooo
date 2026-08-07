package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.ChatStyle
import com.example.data.db.entities.FakeChatEntity
import com.example.data.db.entities.FakeChatMessageEntity
import com.example.ui.CyberScreen
import com.example.ui.CyberViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberGlassCard
import com.example.ui.theme.*

@Composable
fun FakeChatScreen(viewModel: CyberViewModel) {
    val fakeChats by viewModel.fakeChats.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    text = "FAKE CHAT ENGINE",
                    color = NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "${fakeChats.size} CHATS",
                    color = NeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Simulate custom incoming/outgoing chat threads for WhatsApp, Telegram, Messenger, and SMS.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (fakeChats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No fake conversations created.", color = TextMuted, fontSize = 14.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(fakeChats) { chat ->
                        FakeChatItemCard(
                            chat = chat,
                            onClick = {
                                viewModel.navigateTo(CyberScreen.FakeChatDetail(chat.id, chat.contactName))
                            }
                        )
                    }
                }
            }
        }

        // Create Chat FAB
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = NeonGreen,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Chat, contentDescription = "Create Fake Chat")
        }
    }

    if (showCreateDialog) {
        CreateFakeChatDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, phone, style ->
                viewModel.createFakeChat(name, phone, style)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun FakeChatItemCard(chat: FakeChatEntity, onClick: () -> Unit) {
    val styleColor = when (chat.chatStyle) {
        ChatStyle.WHATSAPP -> Color(0xFF25D366)
        ChatStyle.TELEGRAM -> Color(0xFF0088CC)
        ChatStyle.MESSENGER -> Color(0xFF0084FF)
        ChatStyle.SMS -> NeonCyan
    }

    CyberGlassCard(
        borderColor = styleColor.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(styleColor.copy(alpha = 0.2f))
                        .border(1.5.dp, styleColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chat.contactName.take(1).uppercase(),
                        color = styleColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = chat.contactName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = chat.lastMessage, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(styleColor.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = chat.chatStyle.name,
                    color = styleColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CreateFakeChatDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, phone: String, style: ChatStyle) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(ChatStyle.WHATSAPP) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("CREATE FAKE CHAT", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = GlassBorderCyan)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = GlassBorderCyan)
                )

                Text("Select Chat Style Platform:", color = TextSecondary, fontSize = 12.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ChatStyle.values().forEach { style ->
                        val isSelected = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.3f) else GlassSurface)
                                .border(1.dp, if (isSelected) NeonCyan else GlassBorderCyan, RoundedCornerShape(8.dp))
                                .clickable { selectedStyle = style }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = style.name.take(2), color = if (isSelected) NeonCyan else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        onCreate(name, phone, selectedStyle)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
            ) {
                Text("INITIALIZE CHAT", fontWeight = FontWeight.Bold)
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

@Composable
fun FakeChatDetailScreen(
    chatId: Long,
    contactName: String,
    viewModel: CyberViewModel
) {
    val messages by viewModel.repository.getMessagesForChat(chatId).collectAsState(initial = emptyList())
    var inputMessage by remember { mutableStateOf("") }
    var isOutgoingToggle by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GlassSurface)
                .border(1.dp, GlassBorderCyan, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(CyberScreen.Main) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonCyan)
            }
            Text(
                text = contactName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Messages List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val align = if (msg.isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
                val bubbleBg = if (msg.isOutgoing) NeonCyan.copy(alpha = 0.25f) else Color(0xFF1E293B)
                val borderCol = if (msg.isOutgoing) NeonCyan else GlassBorderCyan

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = align
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bubbleBg)
                            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = msg.text, color = TextPrimary, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sender Mode Toggle (Outgoing / Incoming)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Sending Mode:", color = TextSecondary, fontSize = 12.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isOutgoingToggle) NeonCyan.copy(alpha = 0.3f) else GlassSurface)
                        .border(1.dp, if (isOutgoingToggle) NeonCyan else GlassBorderCyan, RoundedCornerShape(8.dp))
                        .clickable { isOutgoingToggle = true }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(text = "Me (Outgoing)", color = if (isOutgoingToggle) NeonCyan else TextMuted, fontSize = 11.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isOutgoingToggle) NeonGreen.copy(alpha = 0.3f) else GlassSurface)
                        .border(1.dp, if (!isOutgoingToggle) NeonGreen else GlassBorderCyan, RoundedCornerShape(8.dp))
                        .clickable { isOutgoingToggle = false }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(text = "Them (Incoming)", color = if (!isOutgoingToggle) NeonGreen else TextMuted, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Type fake message...", color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = GlassBorderCyan),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputMessage.isNotEmpty()) {
                        viewModel.sendFakeChatMessage(chatId, inputMessage, isOutgoingToggle)
                        inputMessage = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(NeonCyan)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}
