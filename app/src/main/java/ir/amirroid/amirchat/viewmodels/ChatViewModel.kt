package ir.amirroid.amirchat.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.helpers.FileNetData
import ir.amirroid.amirchat.data.helpers.DownloadHelper
import ir.amirroid.amirchat.data.helpers.MusicHelper
import ir.amirroid.amirchat.data.helpers.RecorderHelper
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.chat.UserStatus
import ir.amirroid.amirchat.data.models.media.ContactModel
import ir.amirroid.amirchat.data.models.media.FileModel
import ir.amirroid.amirchat.data.models.media.MediaModel
import ir.amirroid.amirchat.data.models.media.MusicModel
import ir.amirroid.amirchat.data.models.media.MusicModelForJson
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.data.repositories.FileRepository
import ir.amirroid.amirchat.data.repositories.chats.ChatRepository
import ir.amirroid.amirchat.data.repositories.users.UsersRepository
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.addAllIf
import ir.amirroid.amirchat.utils.id
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val fileRepository: FileRepository,
    private val musicHelper: MusicHelper,
    private val recorderHelper: RecorderHelper,
    private val chatRepository: ChatRepository,
    private val downloadHelper: DownloadHelper,
    private val userRepository: UsersRepository
) : ViewModel() {

    private val _chats = MutableStateFlow<List<MessageModel>>(emptyList())
    val chats = _chats.asStateFlow()

    val reply = MutableStateFlow<String?>(null)

    val selectedList = MutableStateFlow(emptyList<MessageModel>())


    private val _room = MutableStateFlow<ChatRoom?>(null)
    val room = _room.asStateFlow()

    private val _medias = MutableStateFlow<List<MediaModel>>(emptyList())
    val medias = _medias.asStateFlow()


    private val _files = MutableStateFlow<List<FileModel>>(emptyList())
    val files = _files.asStateFlow()


    private val _contacts = MutableStateFlow<List<ContactModel>>(emptyList())
    val contacts = _contacts.asStateFlow()


    private val _musics = MutableStateFlow<List<MusicModel>>(emptyList())
    val musics = _musics.asStateFlow()


    private val _currentMusic = MutableStateFlow<Uri?>(null)
    val currentMusic = _currentMusic.asStateFlow()
    private var preViewsPlayingMusic: Uri? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _currentRecordingPath = MutableStateFlow("false")
    val currentRecordingPath = _currentRecordingPath.asStateFlow()

    private val _currentTimeAudio = MutableStateFlow(0L)
    val currentTimeAudio = _currentTimeAudio.asStateFlow()

    private val _currentTimePlayingAudio = MutableStateFlow(0L)
    val currentTimePlayingAudio = _currentTimePlayingAudio.asStateFlow()

    private val _showRecordPreview = MutableStateFlow(false)
    val showRecordPreview = _showRecordPreview.asStateFlow()

    var showEmojiKeyboard = MutableStateFlow(false)
    var showKeyboard = MutableStateFlow(true)

    private val _status = MutableStateFlow<UserStatus?>(null)
    val status = _status.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private lateinit var clipboardManager: ClipboardManager

    private var sendingMessages = emptyList<MessageModel>()

    private var toUser = UserModel()

    init {
        observeToMusicStates()
        chatRepository.setScope(viewModelScope)
    }

    companion object {
        val downloadFiles = MutableStateFlow<HashMap<String, FileNetData>>(hashMapOf())
        val uploadFiles = MutableStateFlow<HashMap<String, FileNetData>>(hashMapOf())
    }

    fun observeToChats(
        room: String?,
        user: UserModel
    ) {
        toUser = user
        observeToStatus()
        if (room == null) {
            chatRepository.addRoomWithUser(
                user
            ) { createdRoom ->
                _room.value = createdRoom
                chatRepository.observeToChat(createdRoom.id) {
                    _chats.value = it.toMutableList().addAllIf(sendingMessages) { message ->
                        any { model -> model.id == message.id }.not()
                    }.sortedBy { message -> message.index }
                }
                observeToSending()
            }
        } else {
            _room.value = if (room.split("-").first() == CurrentUser.token) {
                ChatRoom(
                    CurrentUser.user ?: UserModel(),
                    user
                )
            } else {
                ChatRoom(
                    user,
                    CurrentUser.user ?: UserModel()
                )
            }
            chatRepository.observeToChat(room) {
                _chats.value = it.toMutableList().addAllIf(
                    it
                ) { message ->
                    any { model -> model.id == message.id }.not()
                }.sortedBy { message -> message.index }
            }
            observeToSending()
        }
    }

    private fun observeToStatus() {
        chatRepository.observeToStatus(toUser.token) {
            _status.value = it
        }
    }

    private fun observeToSending() = viewModelScope.launch(Dispatchers.IO) {
        chatRepository.getAllSending(_room.value?.id ?: "").collectLatest {
            sendingMessages = it
            _chats.value = _chats.value.toMutableList().addAllIf(sendingMessages) { message ->
                any { model -> model.id == message.id }.not()
            }.sortedBy { message -> message.index }
        }
    }

    fun addMessage(text: String, files: List<FileMessage> = emptyList()) {
        chatRepository.addMessage(
            MessageModel(
                message = text,
                from = CurrentUser.token.toString(),
                chatRoom = _room.value?.id ?: "",
                files = files,
                status = Constants.SEND,
                replyToId = reply.value,
                index = _chats.value.size
            ),
            toUser.fcmToken
        ) { response ->
            uploadFiles.value = uploadFiles.value.apply {
                this[response.url] = response
            }
        }
    }

    private fun startTimer() = viewModelScope.launch {
        _currentTimeAudio.value = 0
        repeat(Int.MAX_VALUE) {
            delay(99)
            if (_isRecording.value.not()) cancel()
            _currentTimeAudio.value += 100
        }
    }

    private fun calculateTimeForPlayingMusic() = viewModelScope.launch {
        _currentTimePlayingAudio.value = 0
        repeat(Int.MAX_VALUE) {
            delay(999)
            if (_currentMusic.value == null) cancel()
            _currentTimePlayingAudio.value = musicHelper.getPosition()
        }
    }

    fun requestRecord() {
        _isRecording.value = true
        startTimer()
        _currentRecordingPath.value = recorderHelper.generatePath()
        recorderHelper.startRecording(_currentRecordingPath.value)
    }

    fun stopRecording(preview: Boolean) {
        _showRecordPreview.value = preview
        _isRecording.value = false
        recorderHelper.stop {
            if (preview.not()) {
                val musicData = MusicModelForJson(
                    _currentRecordingPath.value.split(File.separator).last(),
                    "Unknown",
                    _currentRecordingPath.value,
                    _currentTimeAudio.value,
                    (10000L..10000000L).random(),
                    _currentRecordingPath.value
                )
                addMessage(
                    "",
                    listOf(
                        FileMessage(
                            _currentRecordingPath.value,
                            _currentRecordingPath.value,
                            type = Constants.MUSIC,
                            data = Gson().toJson(musicData)
                        )
                    )
                )
            }
        }
    }

    fun cancelRecording() {
        _isRecording.value = false
        _showRecordPreview.value = false
        playOrPauseMusic(false, File(_currentRecordingPath.value).toUri())
        recorderHelper.cancel(_currentRecordingPath.value)
    }

    private fun observeToMusicStates() {
        musicHelper.observeToStates {
            when (it) {
                ExoPlayer.STATE_ENDED -> {
                    _currentMusic.value = null
                    preViewsPlayingMusic = null
                }
            }
        }
    }

    fun getMedias() {
        fileRepository.getMedias(viewModelScope) {
            _medias.value = it
        }
    }

    fun getFiles() {
        fileRepository.getFile(viewModelScope) {
            _files.value = it
        }
    }

    fun getContacts() {
        fileRepository.getContacts(viewModelScope) {
            _contacts.value = it
        }
    }

    fun getMusics() {
        fileRepository.getMusics(viewModelScope) {
            _musics.value = it
        }
    }


    fun playOrPauseMusic(play: Boolean, uri: Uri, calculateTime: Boolean = false) {
        if (play) {
            if (preViewsPlayingMusic == uri) {
                musicHelper.play()
            } else {
                musicHelper.playWithOutNotification(uri)
            }
            preViewsPlayingMusic = null
            _currentMusic.value = uri
            if (calculateTime) {
                calculateTimeForPlayingMusic()
            }
        } else {
            musicHelper.pause()
            _currentMusic.value = null
            preViewsPlayingMusic = uri
        }
    }

    override fun onCleared() {
        cancelRecording()
        musicHelper.apply {
            clearObservers()
            dispose()
        }
        super.onCleared()
    }

    fun seekTo(seek: Long) {
        musicHelper.seekTo(seek)
    }

    fun playOrPauseMusicWithCache(file: FileMessage, mySend: Boolean, calculateTime: Boolean) {
        if (file.path == _currentMusic.value.toString()) {
            musicHelper.pause()
            _currentMusic.value = null
        } else {
            if (mySend && File(file.fromPath).exists()) {
                if (preViewsPlayingMusic.toString() == file.path) {
                    musicHelper.play()
                } else {
                    musicHelper.playWithOutNotification(file.fromPath.toUri())
                }
            } else {
                if (preViewsPlayingMusic.toString() == file.path) {
                    musicHelper.play()
                } else {
                    musicHelper.playWithCache(file.path)
                }
            }
            _currentMusic.value = file.path.toUri()
            preViewsPlayingMusic = file.path.toUri()
            if (calculateTime) calculateTimeForPlayingMusic()
        }
    }

    fun setEmoji(selectedMessage: MessageModel?, message: String?) {
        val isFrom = _room.value?.from?.token == CurrentUser.token
        chatRepository.setEmoji(
            selectedMessage?.id ?: "",
            message,
            isFrom
        )
    }

    fun copyMessages(messages: List<MessageModel>) {
        if (this::clipboardManager.isInitialized.not()) {
            clipboardManager = context.getSystemService(ClipboardManager::class.java)
        }
        var text = ""
        messages.map { it.message }.forEachIndexed { index, message ->
            text += message
            if (index != selectedList.value.size.minus(1)) {
                text += "\n"
            }
        }
        val clipData = ClipData.newPlainText(context.getString(R.string.app_name), text)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun deleteMessages(messages: List<MessageModel>) {
        chatRepository.deleteChats(messages)
    }

    fun downloadFile(url: String) {
        downloadHelper.downloadWithProgress(url) {
            val newData = downloadFiles.value.apply {
                this[url] = it
            }
            downloadFiles.value = newData
            Log.d("efwwhofw", "downloadFile: $it")
        }
    }

    fun cancelDownload(url: String) {
        downloadHelper.addCancel(url)
    }

    fun setUserStatus(userStatus: UserStatus) {
        chatRepository.setStatus(userStatus)
    }

    fun seenMessage(id: String) {
        chatRepository.setSeen(id)
    }

    fun getUserWithId(id: String, onResponse: (UserModel?) -> Unit) {
        _loading.value = true
        userRepository.getUserWithId(id) {
            _loading.value = false
            onResponse.invoke(it)
        }
    }
}