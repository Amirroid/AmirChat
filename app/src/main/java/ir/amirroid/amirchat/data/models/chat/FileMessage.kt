package ir.amirroid.amirchat.data.models.chat

import ir.amirroid.amirchat.utils.Constants

data class FileMessage(
    val fromPath: String = "",
    val path: String = "",
    val type: Int = Constants.SENDING
)
