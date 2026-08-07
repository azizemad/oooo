package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fake_calls")
data class FakeCallEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val callerName: String,
    val phoneNumber: String,
    val avatarUri: String = "",
    val delaySeconds: Int = 10,
    val ringtoneName: String = "Cyber Matrix",
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
