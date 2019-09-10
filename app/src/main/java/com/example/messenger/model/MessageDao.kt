package com.example.messenger.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MessageDao {
    @get:Query("SELECT * FROM message")
    val all: List<Message>

    @Insert
    fun insertAll(vararg messages: Message)
}