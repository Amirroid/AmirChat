package ir.amirroid.amirchat.data.models.chat

import androidx.media3.common.FileTypes
import ir.amirroid.amirchat.utils.Constants

data class MessageModel(
    val message: String = "",
    val files: List<FileMessage> = emptyList(),
    val status: Int = Constants.SENDING,
    val date: Long = System.currentTimeMillis(),
    val from: String = "",
    val id: String = "",
    val chatRoom: String = ""
)
