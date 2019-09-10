package com.example.messenger.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.databinding.ItemReceivedMessageBinding
import com.example.messenger.databinding.ItemSentMessageBinding
import com.example.messenger.model.Message
import com.example.messenger.viewmodel.ReceivedMessageViewModel
import com.example.messenger.viewmodel.SentMessageViewModel


class MessageListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    private lateinit var messageList:List<Message>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                val binding: ItemSentMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_sent_message, parent, false)
                SentMessageHolder(binding)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                val binding: ItemReceivedMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_received_message, parent, false)
                ReceivedMessageHolder(binding)
            }
            else -> {
                val binding: ItemReceivedMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_received_message, parent, false)
                EmptyMessageHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message, position)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message, position)
        }
    }

    override fun getItemCount(): Int {
        return if(::messageList.isInitialized) messageList.size else 0
    }

    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        return when {
            message.sentMessage -> VIEW_TYPE_MESSAGE_SENT
            else -> VIEW_TYPE_MESSAGE_RECEIVED
        }
    }


    fun updateMessageList(messageList:List<Message>){
        this.messageList = messageList
        notifyDataSetChanged()
    }

    private fun calculateHasTail(position: Int) : Boolean{
        if (position == messageList.size - 1) return true // Most recent message in conversation
        if (messageList[position].senderId != messageList[position+1].senderId) return true // Message before it is send by the other user
        if ((messageList[position].timeStamp - messageList[position-1].timeStamp) > 20000) return true // Time difference is more then 20 seconds
        return false
    }

    inner class SentMessageHolder(private val binding: ItemSentMessageBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = SentMessageViewModel()

        fun bind(message:Message, position: Int){
            viewModel.bind(message, calculateHasTail(position))
            binding.viewModel = viewModel
        }
    }


    inner class ReceivedMessageHolder(private val binding: ItemReceivedMessageBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = ReceivedMessageViewModel()

        fun bind(message:Message, position: Int){
            viewModel.bind(message, calculateHasTail(position))
            binding.viewModel = viewModel
        }
    }

    class EmptyMessageHolder(private val binding: ItemReceivedMessageBinding):RecyclerView.ViewHolder(binding.root)
}