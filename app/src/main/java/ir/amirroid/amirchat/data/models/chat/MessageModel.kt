package ir.amirroid.amirchat.data.models.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.amirroid.amirchat.utils.Constants

@Entity(Constants.SENDING_MESSAGES)
data class MessageModel(
    val message: String = "",
    val files: List<FileMessage> = emptyList(),
    val status: Int = Constants.SENDING,
    val date: Long = System.currentTimeMillis(),
    val from: String = "",
    val chatRoom: String = "",
    @PrimaryKey(autoGenerate = false)
    val id: String = System.currentTimeMillis().toString() + chatRoom,
    val replyToId: String? = null,
    val fromEmoji: String? = null,
    val toEmoji: String? = null,
    val index: Int = 1,
)