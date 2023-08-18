package ir.amirroid.amirchat.data.models.chat

data class ChatRoom(
    val from: String = "",
    val to: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val fromProfile: String,
    val toProfile: String
)