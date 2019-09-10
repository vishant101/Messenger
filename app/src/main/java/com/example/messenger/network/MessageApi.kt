package com.example.messenger.network

import com.example.messenger.model.Message
import io.reactivex.Observable
import retrofit2.http.GET


interface MessageApi {
    @GET("vishant101/resources/posts")
    fun getMessages(): Observable<List<Message>>
}