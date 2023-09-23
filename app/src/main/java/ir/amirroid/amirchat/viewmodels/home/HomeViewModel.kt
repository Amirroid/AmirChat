package ir.amirroid.amirchat.viewmodels.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.auth.AuthManager
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.repositories.chats.ChatRepository
import ir.amirroid.amirchat.data.repositories.users.UsersRepository
import ir.amirroid.amirchat.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    tokenHelper: TokenHelper,
    private val chatRepository: ChatRepository
) : ViewModel() {
    val user = tokenHelper.user

    val connecting = chatRepository.connecting

    private val _rooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val rooms = _rooms.asStateFlow()

    val selectedRooms = mutableStateListOf<ChatRoom>()

    private val _numbersOfChats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val numbersOfChats = _numbersOfChats.asStateFlow()

    init {
        chatRepository.setScope(viewModelScope)
        observeToRooms()
    }

    private fun observeToRooms() {
        chatRepository.observeToRooms {
            _rooms.value = it
            chatRepository.observeToNumberOfMessages(it) { data ->
                _numbersOfChats.value = data
                Log.d("sdfjodj", "observeToRooms: $data")
            }
        }
    }

    fun deleteRoom(room: ChatRoom) {
        chatRepository.deleteRoom(room)
    }

    override fun onCleared() {
        chatRepository.onDestroy()
        super.onCleared()
    }

    fun toggleRoom(room: ChatRoom){
        if (selectedRooms.contains(room)){
            selectedRooms.remove(room)
        }else{
            selectedRooms.add(room)
        }
        Log.d("sdfsf", "toggleRoom: ${selectedRooms}")
    }
}