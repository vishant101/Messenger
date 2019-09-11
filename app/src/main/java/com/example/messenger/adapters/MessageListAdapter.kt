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
import androidx.recyclerview.widget.DiffUtil
import com.example.messenger.utils.TAIL_CHANGE


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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        val set = payloads.firstOrNull() as Set<String>?

        when {
            set == null || set.isEmpty() -> return super.onBindViewHolder(holder, position, payloads)
            set.contains(TAIL_CHANGE) -> {
                when (holder.itemViewType) {
                    VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).updateTail(messageSectionList[position].hasTail!!)
                    VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).updateTail(messageSectionList[position].hasTail!!)
                }
            }
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
        if (itemCount == 0) {
            this.messageSectionList = messageSectionList
            notifyDataSetChanged()
        } else {
            updateMessageSectionListData(messageSectionList)
        }
    }

    private fun updateMessageSectionListData(messageSectionList: List<MessageSection>){
        val diffResult = DiffUtil.calculateDiff(ConversionListDiff(this.messageSectionList, messageSectionList))
        this.messageSectionList = messageSectionList
        diffResult.dispatchUpdatesTo(this)
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

        fun updateTail(hashTail: Boolean){
            viewModel.updateHasTail(hashTail)
        }
    }

    inner class ReceivedMessageHolder(private val binding: ItemReceivedMessageBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = ReceivedMessageViewModel()

        fun bind(messageSection: MessageSection){
            viewModel.bind(messageSection.message!!, messageSection.hasTail!!)
            binding.viewModel = viewModel
        }

        fun updateTail(hashTail: Boolean){
            viewModel.updateHasTail(hashTail)
        }
    }

    class EmptyMessageHolder(private val binding: ItemSectionBreakBinding):RecyclerView.ViewHolder(binding.root)

    inner class ConversionListDiff(private val oldList: List<MessageSection>, private val newList: List<MessageSection>) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
            val payloadSet = mutableSetOf<String>()
            when { oldList[oldPosition].hasTail != newList[newPosition].hasTail -> payloadSet.add(TAIL_CHANGE) }

            return payloadSet
        }
    }
}