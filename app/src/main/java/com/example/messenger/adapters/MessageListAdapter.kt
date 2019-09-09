package com.example.messenger.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.databinding.ItemMessageBinding
import com.example.messenger.model.Message
import com.example.messenger.viewmodel.MessageViewModel


class MessageListAdapter: RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    private lateinit var messageList:List<Message>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListAdapter.ViewHolder {
        val binding: ItemMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_message, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return if(::messageList.isInitialized) messageList.size else 0
    }

    fun updateMessageList(messageList:List<Message>){
        this.messageList = messageList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemMessageBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = MessageViewModel()

        fun bind(message:Message){
            viewModel.bind(message)
            binding.viewModel = viewModel
        }
    }
}