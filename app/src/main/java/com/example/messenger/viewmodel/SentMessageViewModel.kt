package com.example.messenger.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.messenger.model.Message

class SentMessageViewModel:BaseViewModel() {
   private val messageText = MutableLiveData<String>()
   private val tail = MutableLiveData<Boolean>()

   fun bind(message: Message, tail: Boolean){
      messageText.value = message.messageText
      this.tail.value = tail
   }

   fun getMessageText(): MutableLiveData<String> {
      return messageText
   }

   fun getHasTail(): MutableLiveData<Boolean> {
      return tail
   }
}