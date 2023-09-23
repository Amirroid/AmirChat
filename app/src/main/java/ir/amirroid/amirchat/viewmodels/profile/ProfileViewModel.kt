package ir.amirroid.amirchat.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.chat.UserStatus
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.data.repositories.chats.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _status = MutableStateFlow<UserStatus?>(null)
    val status = _status.asStateFlow()

    private val _room = MutableStateFlow<ChatRoom?>(null)
    val room = _room.asStateFlow()

    fun observeToStatus(token: String) {
        chatRepository.observeToStatus(token) {
            _status.value = it
        }
    }

    fun connectToRooms(user: UserModel) = viewModelScope.launch(Dispatchers.IO) {
        chatRepository.localData.rooms.collectLatest {
            val rooms = Gson().fromJson(it, Array<ChatRoom>::class.java)
            rooms.firstOrNull { chatRoom -> chatRoom.from.token == user.token || chatRoom.to.token == user.token }
                .let { fRoom ->
                    _room.value = fRoom
                }
        }
    }

    fun setMyRoomNotificationEnabled(enabled: Boolean) {
        _room.value?.let {
            chatRepository.setMyNotificationEnabled(enabled, it)
        }
    }
}