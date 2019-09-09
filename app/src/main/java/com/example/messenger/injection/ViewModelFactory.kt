package com.example.messenger.injection

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.messenger.model.database.AppDatabase
import com.example.messenger.viewmodel.MessageListViewModel


class ViewModelFactory(private val activity: AppCompatActivity): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageListViewModel::class.java)) {
            val db = Room.databaseBuilder(activity.applicationContext, AppDatabase::class.java, "messages").build()
            @Suppress("UNCHECKED_CAST")
            return MessageListViewModel(db.messagesDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}