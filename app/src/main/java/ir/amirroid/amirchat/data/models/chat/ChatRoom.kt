package ir.amirroid.amirchat.data.models.chat

import ir.amirroid.amirchat.data.models.register.UserModel


data class ChatRoom(
    val from: UserModel = UserModel(),
    val to: UserModel = UserModel(),
    val createdDate: Long = System.currentTimeMillis(),
)