package com.example.messenger.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.messenger.model.MessageSection
import java.util.*

class SectionBreakViewModel:BaseViewModel() {
    private val dateString = MutableLiveData<String>()

    fun bind(messageSection: MessageSection){
        val date = Date(messageSection.epochTime)
        dateString.value = date.toString()
    }

    fun getDate(): MutableLiveData<String> {
        return dateString
    }
}