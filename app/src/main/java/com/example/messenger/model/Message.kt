package com.example.messenger.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class Message (
    @PrimaryKey
    val senderId: Int,
    val name: String,
    var messageText: String,
    var timestamp: Int
)