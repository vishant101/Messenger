package com.example.messenger.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.messenger.R
import com.example.messenger.adapters.MessageListAdapter
import com.example.messenger.model.Message
import com.example.messenger.model.MessageDao
import com.example.messenger.network.MessageApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MessageListViewModel(private val messageDao: MessageDao):BaseViewModel(){
    @Inject
    lateinit var messageApi: MessageApi
    val messageListAdapter: MessageListAdapter = MessageListAdapter()

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadMessages() }

    private lateinit var subscription: Disposable

    init{
        loadMessages()
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    private fun loadMessages(){
        subscription = Observable.fromCallable { messageDao.all }
            .concatMap {
                    dbMessageList ->
                if(dbMessageList.isEmpty())
                    messageApi.getMessages().concatMap {
                            apiMessageList -> messageDao.insertAll(*apiMessageList.toTypedArray())
                        Observable.just(apiMessageList)
                    }
                else
                    Observable.just(dbMessageList)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveMessageListStart() }
            .doOnTerminate { onRetrieveMessageListFinish() }
            .subscribe(
                { result -> onRetrieveMessageListSuccess(result) },
                { error -> onRetrieveMessageListError(error) }
            )
    }

    private fun onRetrieveMessageListStart(){
        loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    private fun onRetrieveMessageListFinish(){
        loadingVisibility.value = View.GONE
    }

    private fun onRetrieveMessageListSuccess(messageList:List<Message>){
        for (item in messageList){
            Log.i(item.senderName, item.messageText)
        }
        messageListAdapter.updateMessageList(messageList)
    }

    private fun onRetrieveMessageListError(error: Any?){
        Log.e("ERROR", error.toString())
        errorMessage.value = R.string.message_error
    }
}