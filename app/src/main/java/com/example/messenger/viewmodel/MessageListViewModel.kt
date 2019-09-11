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
import com.example.messenger.utils.USER_ID
import com.example.messenger.utils.USER_NAME
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
    var toastStatus = MutableLiveData<Boolean?>()


    private val enteredUserMessage = MutableLiveData<String>()
    private val messageList: MutableList<Message> = mutableListOf()
    private val messageSectionList: MutableList<MessageSection> = mutableListOf()

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
            this.messageList.add(item)
        }
        createMessageSectionList()
        messageListAdapter.updateMessageList(messageSectionList)
    }

    private fun onRetrieveMessageListError(error: Any?){
        Log.e("ERROR", error.toString())
        errorMessage.value = R.string.message_error
    }

    private fun createMessageSectionList() {
        updateMessageSelectionList(0)
    }

    private fun updateMessageSelectionList(start: Int){
        for (i in start until messageList.size){
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
    }

    private fun getViewType(message: Message): Int{
        return when (USER_ID) {
            message.senderId -> VIEW_TYPE_MESSAGE_SENT
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

    fun sendMessage() {
        if (enteredUserMessage.value == null || enteredUserMessage.value == ""){
            this.toastStatus.value = true
            return
        }
        val timeStamp = System.currentTimeMillis() / 1000
        val messageId = this.messageList[messageList.size-1].messageId + 1
        val message = Message( messageId, USER_ID, USER_NAME, enteredUserMessage.value!!, timeStamp)

        // Clear the text box
        enteredUserMessage.value = ""

        // Ideally we want to send a message to a service and snyc our messageList
        // For now we will just update our local messages
        this.messageList.add(message)
        updateMessageSelectionList(messageList.size-1)
        messageListAdapter.updateMessageList(messageSectionList)
        messageListAdapter.scrollTo.set(messageSectionList.size-1)
    }

    fun getEnteredMessage(): MutableLiveData<String> {
        return enteredUserMessage
    }

    fun onEditTextChange(s: CharSequence) {
        enteredUserMessage.value = s.toString()
    }
}