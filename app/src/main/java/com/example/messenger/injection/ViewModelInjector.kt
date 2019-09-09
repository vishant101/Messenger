package com.example.messenger.injection

import androidx.core.view.KeyEventDispatcher
import com.example.messenger.network.NetworkModule
import com.example.messenger.viewmodel.MessageListViewModel
import com.example.messenger.viewmodel.MessageViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector {

    fun inject(messageListViewModel: MessageListViewModel)

    fun inject(messageViewModel: MessageViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}