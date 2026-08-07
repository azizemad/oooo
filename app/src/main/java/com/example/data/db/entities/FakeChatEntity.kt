package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ChatStyle {
    WHATSAPP, TELEGRAM, MESSENGER, SMS
}

@Entity(tableName = "fake_chats")
data class FakeChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactName: String,
    val phoneNumber: String = "",
    val avatarUri: String = "",
    val chatStyle: ChatStyle = ChatStyle.WHATSAPP,
    val lastMessage: String = "",
    val lastTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "fake_chat_messages")
data class FakeChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: Long,
    val text: String,
    val isOutgoing: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
