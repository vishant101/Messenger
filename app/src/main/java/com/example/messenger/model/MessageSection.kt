package com.example.messenger.model

data class MessageSection (
    val messageType: Int,
    val message: Message?,
    val hasTail: Boolean?,
    val epochTime: Long
) {
    companion object Types {
        const val VIEW_TYPE_MESSAGE_SENT = 1
        const val VIEW_TYPE_MESSAGE_RECEIVED = 2
        const val VIEW_TYPE_SECTION_BREAK = 3
    }
}
