package ir.amirroid.amirchat.data.models.chat

data class FileMessage(
    val fromPath: String = "",
    val path: String = "",
    val type: Int = -1,
    val data: String = "",
    val reference:String = ""
)
