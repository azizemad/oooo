package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CallType {
    INCOMING, OUTGOING, MISSED, BLOCKED
}

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val callerName: String = "",
    val callType: CallType = CallType.INCOMING,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val simSlot: Int = 1
)
