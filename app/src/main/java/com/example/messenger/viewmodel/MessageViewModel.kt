package com.example.messenger.viewmodel

import com.example.messenger.model.Message


open class MessageViewModel:BaseViewModel() {

    open fun bind(message: Message) {}
}