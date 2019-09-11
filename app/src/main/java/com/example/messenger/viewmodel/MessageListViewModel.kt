package com.example.messenger.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.messenger.R
import com.example.messenger.adapters.MessageListAdapter
import com.example.messenger.model.Message
import com.example.messenger.model.MessageDao
import com.example.messenger.model.MessageSection
import com.example.messenger.model.MessageSection.Types.VIEW_TYPE_MESSAGE_RECEIVED
import com.example.messenger.model.MessageSection.Types.VIEW_TYPE_MESSAGE_SENT
import com.example.messenger.model.MessageSection.Types.VIEW_TYPE_SECTION_BREAK
import com.example.messenger.network.MessageApi
import com.example.messenger.utils.EPOCH_TWENTY_SECONDS
import com.example.messenger.utils.EPOCh_TWO_HOURS
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

    private fun getMessageSectionList(messageList: List<Message>): MutableList<MessageSection> {
        val messageSectionList: MutableList<MessageSection> = mutableListOf()
        for (i in 0 until messageList.size){
            val message = messageList[i]
            val viewType = getViewType(message)
            val hasTale = calculateHasTail(position = i, messageList = messageList)
            val epochTime = message.timeStamp
            
            if (i == 0) {
                // If first item add a section break
                messageSectionList.add(MessageSection(VIEW_TYPE_SECTION_BREAK, null, null, epochTime))
            } else {
                // If time diff > two hours add a section break
                val timeDiff = messageList[i].timeStamp - messageList[i-1].timeStamp
                if (timeDiff > EPOCh_TWO_HOURS) {
                    messageSectionList.add(MessageSection(VIEW_TYPE_SECTION_BREAK, null, null, epochTime))
                }
            }

            // Add the messages
            messageSectionList.add(MessageSection(viewType, message, hasTale, epochTime))
        }

        return messageSectionList
    }

    private fun onRetrieveMessageListSuccess(messageList:List<Message>){
        val messageSectionList = getMessageSectionList(messageList)
        messageListAdapter.updateMessageList(messageSectionList)
    }

    private fun getViewType(message: Message): Int{
        return when {
            message.sentMessage -> VIEW_TYPE_MESSAGE_SENT
            else -> VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    private fun calculateHasTail(position: Int, messageList: List<Message>) : Boolean{
        if (position == messageList.size - 1) return true // Most recent message in conversation
        if (messageList[position].senderId != messageList[position+1].senderId) return true // Message before it is send by the other user
        val timeDiff = messageList[position+1].timeStamp - messageList[position].timeStamp
        if (timeDiff > EPOCH_TWENTY_SECONDS) return true // Time difference is more then 20 seconds
        return false
    }



    private fun onRetrieveMessageListError(error: Any?){
        Log.e("ERROR", error.toString())
        errorMessage.value = R.string.message_error
    }
}