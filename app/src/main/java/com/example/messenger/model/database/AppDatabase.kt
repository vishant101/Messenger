package com.example.messenger.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.messenger.model.Message
import com.example.messenger.model.MessageDao

@Database(entities = [Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessageDao
}