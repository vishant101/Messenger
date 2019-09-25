package com.example.messenger.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.messenger.model.MessageSection
import java.text.SimpleDateFormat
import java.util.*


class SectionBreakViewModel:BaseViewModel() {
    private val dayString = MutableLiveData<String>()
    private val timeString = MutableLiveData<String>()


    fun bind(messageSection: MessageSection){
        val date = Date(messageSection.epochTime*1000)
        val dayFormat = SimpleDateFormat("EEEE ")
        val timeFormat = SimpleDateFormat("HH:mm")
        dayString.value = dayFormat.format(date)
        timeString.value = timeFormat.format(date)
    }

    fun getDay(): MutableLiveData<String> {
        return dayString
    }

    fun getTime(): MutableLiveData<String> {
        return timeString
    }
}