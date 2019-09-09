package com.example.messenger.network

import com.example.messenger.model.Message
import io.reactivex.Observable
import retrofit2.http.GET


interface MessageApi {
    @GET("/messages")
    fun getMessages(): Observable<List<Message>>
}