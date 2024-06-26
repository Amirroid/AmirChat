package ir.amirroid.amirchat.data.events

import androidx.compose.ui.geometry.Offset
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.UserModel

sealed class MessageEvents {
    class SeekExo(val position: Long) : MessageEvents()
    class Reply(val id: String) : MessageEvents()
    class LongClick(val messageModel: MessageModel) : MessageEvents()
    class Click(val messageModel: MessageModel, val offset: Offset) : MessageEvents()

    class SetEmoji(val messageModel: MessageModel, val emoji: String?) : MessageEvents()

    class DownloadFile(val path:String)  :MessageEvents()
    class CancelDownload(val path:String)  :MessageEvents()

    class Seen(val id:String) : MessageEvents()

    class OpenId(val id:String) : MessageEvents()

    class OpenUser(val user:UserModel) : MessageEvents()
}
