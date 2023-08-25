package ir.amirroid.amirchat.data.models.chat

import ir.amirroid.amirchat.utils.Constants

data class MessageModel(
    val message: String = "",
    val files: List<FileMessage> = emptyList(),
    val status: Int = Constants.SENDING,
    val date: Long = System.currentTimeMillis(),
    val from: String = "",
    val chatRoom: String = "",
    val id: String = System.currentTimeMillis().toString() + chatRoom,
    val replyToId: String? = null,
)
