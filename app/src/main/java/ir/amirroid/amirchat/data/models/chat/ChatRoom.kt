package ir.amirroid.amirchat.data.models.chat

import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel


data class ChatRoom(
    val from: UserModel = UserModel(),
    val to: UserModel = UserModel(),
    val createdDate: Long = System.currentTimeMillis(),
    val lastMessage: String = "",
    val lastTime: Long = 0L,
    val fromNumberOfMessages: Int = 0,
    val toNumberOfMessages: Int = 0,
    val fromNotificationEnabled: Boolean = true,
    val toNotificationEnabled: Boolean = true,
) {
    fun myNotificationEnabled() = if (from.token == CurrentUser.token) {
        fromNotificationEnabled
    } else toNotificationEnabled

    fun getUser() = if (from.token == CurrentUser.token) {
        from
    } else to
}