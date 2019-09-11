package com.example.messenger.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class Message (
    @PrimaryKey
    val messageId: Int,
    val senderId: Int,
    val senderName: String,
    var messageText: String,
    var timeStamp: Long          // Epoch Time
)