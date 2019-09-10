package com.example.messenger.injection

import com.example.messenger.network.NetworkModule
import com.example.messenger.viewmodel.MessageListViewModel
import com.example.messenger.viewmodel.ReceivedMessageViewModel
import com.example.messenger.viewmodel.SentMessageViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector {

    fun inject(messageListViewModel: MessageListViewModel)

    fun inject(sentMessageViewModel: SentMessageViewModel)

    fun inject(receivedMessageViewModel: ReceivedMessageViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}