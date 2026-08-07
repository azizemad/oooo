package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val email: String = "",
    val avatarUri: String = "",
    val isFavorite: Boolean = false,
    val category: String = "General",
    val colorHex: String = "#00F0FF",
    val note: String = ""
)
