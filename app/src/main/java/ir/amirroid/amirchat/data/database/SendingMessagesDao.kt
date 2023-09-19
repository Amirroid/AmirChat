package ir.amirroid.amirchat.data.database

import androidx.room.Dao
import androidx.room.Query
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.utils.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface SendingMessagesDao : BaseDao<MessageModel> {
    @Query("SELECT * FROM ${Constants.SENDING_MESSAGES} WHERE chatRoom == :room")
    fun getAll(room:String) : Flow<List<MessageModel>>
}