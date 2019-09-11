package com.example.messenger.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.databinding.ItemReceivedMessageBinding
import com.example.messenger.databinding.ItemSectionBreakBinding
import com.example.messenger.databinding.ItemSentMessageBinding
import com.example.messenger.model.MessageSection
import com.example.messenger.model.MessageSection.Types.VIEW_TYPE_MESSAGE_RECEIVED
import com.example.messenger.model.MessageSection.Types.VIEW_TYPE_MESSAGE_SENT
import com.example.messenger.model.MessageSection.Types.VIEW_TYPE_SECTION_BREAK
import com.example.messenger.viewmodel.ReceivedMessageViewModel
import com.example.messenger.viewmodel.SectionBreakViewModel
import com.example.messenger.viewmodel.SentMessageViewModel
import androidx.databinding.ObservableInt




class MessageListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var messageSectionList: List<MessageSection>
    var scrollTo = ObservableInt()

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
            VIEW_TYPE_SECTION_BREAK -> {
                val binding: ItemSectionBreakBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_section_break, parent, false)
                SectionBreakViewHolder(binding)
            }
            else -> {
                val binding: ItemSectionBreakBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_section_break, parent, false)
                EmptyMessageHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageSection = messageSectionList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(messageSection)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(messageSection)
            VIEW_TYPE_SECTION_BREAK -> (holder as SectionBreakViewHolder).bind(messageSection)
        }
    }

    override fun getItemCount(): Int {
        return if(::messageSectionList.isInitialized) messageSectionList.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        val messageSection = messageSectionList[position]
        return messageSection.messageType
    }

    fun updateMessageList(messageSectionList:List<MessageSection>){
        this.messageSectionList = messageSectionList
        notifyDataSetChanged()
    }

    inner class SectionBreakViewHolder(private val binding: ItemSectionBreakBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = SectionBreakViewModel()

        fun bind(messageSection: MessageSection){
            viewModel.bind(messageSection)
            binding.viewModel = viewModel
        }
    }

    inner class SentMessageHolder(private val binding: ItemSentMessageBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = SentMessageViewModel()

        fun bind(messageSection: MessageSection){
            viewModel.bind(messageSection.message!!, messageSection.hasTail!!)
            binding.viewModel = viewModel
        }
    }

    inner class ReceivedMessageHolder(private val binding: ItemReceivedMessageBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = ReceivedMessageViewModel()

        fun bind(messageSection: MessageSection){
            viewModel.bind(messageSection.message!!, messageSection.hasTail!!)
            binding.viewModel = viewModel
        }
    }

    class EmptyMessageHolder(private val binding: ItemSectionBreakBinding):RecyclerView.ViewHolder(binding.root)
}