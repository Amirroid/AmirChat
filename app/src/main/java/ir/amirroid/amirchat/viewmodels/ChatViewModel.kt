package ir.amirroid.amirchat.viewmodels

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.helpers.MusicHelper
import ir.amirroid.amirchat.data.helpers.RecorderHelper
import ir.amirroid.amirchat.data.models.media.ContactModel
import ir.amirroid.amirchat.data.models.media.FileModel
import ir.amirroid.amirchat.data.models.media.MediaModel
import ir.amirroid.amirchat.data.models.media.MusicModel
import ir.amirroid.amirchat.data.repositories.FileRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val fileRepository: FileRepository,
    private val musicHelper: MusicHelper,
    private val recorderHelper: RecorderHelper
) : ViewModel() {
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


    init {
        observeToMusicStates()
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
        recorderHelper.stop()
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

}