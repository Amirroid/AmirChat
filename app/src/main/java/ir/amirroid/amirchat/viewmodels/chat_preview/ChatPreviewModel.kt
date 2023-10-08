package ir.amirroid.amirchat.viewmodels.chat_preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.chat.UserStatus
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.data.repositories.chats.ChatRepository
import ir.amirroid.amirchat.utils.addAllIf
import ir.amirroid.amirchat.utils.id
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatPreviewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _chats = MutableStateFlow(emptyList<MessageModel>())
    val chats = _chats.asStateFlow()

    private val _status = MutableStateFlow<UserStatus?>(null)
    val status = _status.asStateFlow()

    private var sendingMessages = emptyList<MessageModel>()

    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }

    init {
        chatRepository.setScope(viewModelScope)
    }

    fun observeToRoom(room: String, user: String) = viewModelScope.launch(Dispatchers.IO + job) {
        chatRepository.observeToChat(room) {
            _chats.value = it.toMutableList().addAllIf(sendingMessages) { message ->
                any { model -> model.id == message.id }.not()
            }.sortedBy { message -> message.index }
        }
        if (room==CurrentUser.token){
            chatRepository.observeToStatus(user){
                _status.value = it
            }
        }
        observeToSending(room)
    }

    fun disconnect() {
        job.cancel()
    }

    private suspend fun observeToSending(room: String) {
        chatRepository.getAllSending(room).collectLatest {
            sendingMessages = it
            _chats.value = _chats.value.toMutableList().addAllIf(sendingMessages) { message ->
                any { model -> model.id == message.id }.not()
            }.sortedBy { message -> message.index }
        }
    }
}