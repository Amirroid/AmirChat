package ir.amirroid.amirchat.viewmodels.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val tokenHelper: TokenHelper,
    private val chatRepository: ChatRepository
) : ViewModel() {
    val image = tokenHelper.image.map { it ?: "" }
    val mobile = tokenHelper.mobile.map { it ?: "" }
    val firstName = tokenHelper.firstName.map { it ?: "" }
    val lastName = tokenHelper.lastName.map { it ?: "" }

    private val _rooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val rooms = _rooms.asStateFlow()

    init {
        observeToRooms()
    }

    private fun observeToRooms() {
        chatRepository.observeToRooms(viewModelScope) {
            _rooms.value = it
        }
    }

    fun deleteRoom(room:ChatRoom) {
        chatRepository.deleteRoom(room)
    }
}