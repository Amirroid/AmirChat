package ir.amirroid.amirchat.data.helpers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.preferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalData @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val dataStore = context.preferences

    object Keys {
        val rooms = stringPreferencesKey(Constants.ROOMS)
    }

    val rooms = dataStore.data.map {
        it[Keys.rooms]
    }


    suspend fun setRooms(room: String) {
        dataStore.edit {
            it[Keys.rooms] = room
        }
    }

    suspend fun setChatsFromRoom(roomID: String, chats: List<MessageModel>) {
        if (chats.isNotEmpty()) {
            val key = stringPreferencesKey(roomID)
            val json = Gson().toJson(chats)
            dataStore.edit {
                it[key] = json
            }
        }
    }

    suspend fun collectToChatsFromRoom(roomID: String, onValue: (List<MessageModel>) -> Unit) {
        val key = stringPreferencesKey(roomID)
        dataStore.data.map {
            it[key]
        }.collectLatest {
            if (it.isNullOrEmpty().not()) {
                onValue.invoke(
                    Gson().fromJson(it, Array<MessageModel>::class.java).toList()
                )
            }
        }
    }


    suspend fun getChats(roomID: String): List<MessageModel> {
        val key = stringPreferencesKey(roomID)
        return Gson().fromJson(dataStore.data.map {
            it[key]
        }.firstOrNull(), Array<MessageModel>::class.java).toList()
    }

    suspend fun deleteRoom(room: ChatRoom) {
        try {
            val rooms =
                Gson().fromJson(rooms.first() ?: "", Array<ChatRoom>::class.java).toMutableList().apply {
                    remove(room)
                }
            dataStore.edit {
                it[Keys.rooms] = Gson().toJson(rooms)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteChats(room: String) {
        val key = stringPreferencesKey(room)
        dataStore.edit {
            it.remove(key)
        }
    }
}