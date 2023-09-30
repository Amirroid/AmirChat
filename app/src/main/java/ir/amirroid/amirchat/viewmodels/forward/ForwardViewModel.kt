package ir.amirroid.amirchat.viewmodels.forward

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.helpers.LocalData
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.repositories.chats.ChatRepository
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.id
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForwardViewModel @Inject constructor(
    private val localData: LocalData,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _rooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val rooms = _rooms.asStateFlow()

    val selectedRooms = mutableStateListOf<ChatRoom>()

    var loading by mutableStateOf(false)

    init {
        getChatRooms()
        chatRepository.setScope(viewModelScope)
    }

    private fun getChatRooms() = viewModelScope.launch(Dispatchers.IO) {
        localData.rooms.map {
            Gson().fromJson(it, Array<ChatRoom>::class.java).toList()
        }
            .collectLatest {
                _rooms.value = it
            }
    }

    fun toggleRoom(room: ChatRoom) {
        if (selectedRooms.contains(room)) {
            selectedRooms.remove(room)
        } else {
            selectedRooms.add(room)
        }
    }

    fun sendMessages(messages: List<MessageModel>, onEnd: () -> Unit) {
        loading = true
        selectedRooms.forEachIndexed { i, room ->
            messages.forEach { message ->
                chatRepository.addMessageWithOutFiles(
                    message.copy(
                        status = Constants.SEND,
                        from = CurrentUser.token ?: "",
                        date = System.currentTimeMillis(),
                        fromEmoji = null,
                        toEmoji = null,
                        id = System.currentTimeMillis().toString() + room.id,
                        chatRoom = room.id,
                    ),
                    room.getToChatUser().fcmToken,
                ) {
                    if (i.plus(1) == selectedRooms.size) {
                        onEnd.invoke()
                        loading = false
                    }
                }
            }
        }
    }
}