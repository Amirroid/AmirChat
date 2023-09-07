package ir.amirroid.amirchat.data.events

import androidx.compose.ui.geometry.Offset
import ir.amirroid.amirchat.data.models.chat.MessageModel

sealed class MessageEvents {
    class SeekExo(val position: Long) : MessageEvents()
    class Reply(val id: String) : MessageEvents()
    class LongClick(val messageModel: MessageModel) : MessageEvents()
    class Click(val messageModel: MessageModel, val offset: Offset) : MessageEvents()

    class SetEmoji(val messageModel: MessageModel, val emoji: String?) : MessageEvents()

    class DownloadFile(val path:String)  :MessageEvents()
    class CancelDownload(val path:String)  :MessageEvents()
}
