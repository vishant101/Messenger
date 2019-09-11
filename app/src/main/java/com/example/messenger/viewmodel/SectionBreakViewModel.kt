package com.example.messenger.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.messenger.model.MessageSection
import java.text.SimpleDateFormat
import java.util.*

class SectionBreakViewModel:BaseViewModel() {
    private val dateString = MutableLiveData<String>()

    fun bind(messageSection: MessageSection){
        val date = Date(messageSection.epochTime)
        val dateFormat = SimpleDateFormat("E HH:mm")
        dateString.value = dateFormat.format(date) //date.toString()
    }

    fun getDate(): MutableLiveData<String> {
        return dateString
    }
}