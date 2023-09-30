package ir.amirroid.amirchat.ui.features.chat

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.gson.Gson
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.events.MessageEvents
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.chat.UserStatus
import ir.amirroid.amirchat.data.models.media.MusicModel
import ir.amirroid.amirchat.data.models.media.MusicModelForJson
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.ui.components.AudioRecordedPreview
import ir.amirroid.amirchat.ui.components.AudioRecorderField
import ir.amirroid.amirchat.ui.components.ChatMediaPopUp
import ir.amirroid.amirchat.ui.components.FileSelectorBottomSheet
import ir.amirroid.amirchat.ui.components.LoadingDialog
import ir.amirroid.amirchat.ui.components.MessagePopUp
import ir.amirroid.amirchat.ui.components.MessagePopUpEvent
import ir.amirroid.amirchat.ui.components.MessagesList
import ir.amirroid.amirchat.ui.components.StickerTextField
import ir.amirroid.amirchat.ui.components.TextAnimation
import ir.amirroid.amirchat.ui.components.TextChangeAnimation
import ir.amirroid.amirchat.ui.components.TextChangeAnimationCounter
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.utils.id
import ir.amirroid.amirchat.utils.startLongPress
import ir.amirroid.amirchat.viewmodels.ChatViewModel
import ir.amirroid.emojikeyboard2.EmojiKeyboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ChatScreen(room: String?, user: UserModel, navigation: NavController) {
    val viewModel: ChatViewModel = hiltViewModel()
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val lazyState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var popUpChat by remember {
        mutableStateOf(Offset.Zero)
    }
    var selectedMessage by remember {
        mutableStateOf<MessageModel?>(null)
    }
    var filePopupShow by remember {
        mutableStateOf(false)
    }
    val selectedList by viewModel.selectedList.collectAsStateWithLifecycle()
    val replyId by viewModel.messageIdForReplyAndEdit.collectAsStateWithLifecycle()
    var popUpMedia by remember {
        mutableStateOf<Pair<MessageModel, FileMessage>?>(null)
    }
    var showChatMedia by remember {
        mutableStateOf(false)
    }
    var offsetChatMedia by remember {
        mutableStateOf(Offset.Zero)
    }
    var sizeChatMedia by remember {
        mutableStateOf(Size.Zero)
    }
    val messages by viewModel.chats.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val currentTimeAudio by viewModel.currentTimeAudio.collectAsStateWithLifecycle()
    val currentTimePlayingAudio by viewModel.currentTimePlayingAudio.collectAsStateWithLifecycle()
    val showRecordPreview by viewModel.showRecordPreview.collectAsStateWithLifecycle()
    val currentPathRecording by viewModel.currentRecordingPath.collectAsStateWithLifecycle()
    val currentMusic by viewModel.currentMusic.collectAsStateWithLifecycle()
    val currentRoom by viewModel.room.collectAsStateWithLifecycle()
    val downloadFiles by ChatViewModel.downloadFiles.collectAsStateWithLifecycle()
    val uploadFiles by ChatViewModel.uploadFiles.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val showEmojiKeyboard by viewModel.showEmojiKeyboard.collectAsStateWithLifecycle()
    val showKeyboard by viewModel.showKeyboard.collectAsStateWithLifecycle()
    val generatedRoom by viewModel.room.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val snackBarState = remember {
        SnackbarHostState()
    }
    val hapticFeedback = LocalHapticFeedback.current
    DisposableEffect(key1 = Unit) {
        viewModel.observeToChats(room, user)
        onDispose { focusManager.clearFocus() }
    }
    LaunchedEffect(key1 = messages.size) {
        delay(200)
        lazyState.animateScrollToItem(0)
        hapticFeedback.startLongPress()
    }
    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(key1 = showEmojiKeyboard, key2 = imeVisible) {
        viewModel.setUserStatus(
            UserStatus(
                true,
                System.currentTimeMillis(),
                user.token,
                if (imeVisible || showEmojiKeyboard) Constants.TYPING else null
            )
        )
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarState)
        },
        bottomBar = {
            AnimatedContent(targetState = showRecordPreview, transitionSpec = {
                slideInVertically { 200 } + fadeIn() with slideOutVertically { 200 } + fadeOut()
            }, label = "") {
                if (it) {
                    val uri = Uri.fromFile(File(currentPathRecording))
                    AudioRecordedPreview(duration = currentTimeAudio,
                        position = currentTimePlayingAudio,
                        playing = uri == currentMusic,
                        onPlayRequest = { play ->
                            viewModel.playOrPauseMusic(play, uri, calculateTime = true)
                        },
                        onValueChanged = { seek -> viewModel.seekTo(seek) },
                        onDelete = { viewModel.cancelRecording() }) {
                        val musicData = MusicModelForJson(
                            currentPathRecording.split(File.separator).last(),
                            "Unknown",
                            currentPathRecording,
                            currentTimeAudio,
                            (10000L..10000000L).random(),
                            currentPathRecording
                        )
                        viewModel.addMessage(
                            "",
                            listOf(
                                FileMessage(
                                    currentPathRecording,
                                    currentPathRecording,
                                    type = Constants.MUSIC,
                                    data = Gson().toJson(musicData)
                                )
                            )
                        )
                    }
                } else {
                    if (isRecording) {
                        AudioRecorderField(timeRecording = currentTimeAudio, onCancel = {
                            viewModel.cancelRecording()
                        }) { preview ->
                            viewModel.stopRecording(preview)
                        }
                    } else {
                        TextFieldChat(selectedMessage = if (replyId == null) null else messages.firstOrNull { message -> message.id == replyId?.second },
                            isEdit = replyId?.first == true,
                            user,
                            onReplyCancel = {
                                viewModel.messageIdForReplyAndEdit.value = null
                            },
                            onRecord = { viewModel.requestRecord() },
                            onFileRequest = {
                                viewModel.getMedias()
                                filePopupShow = true
                            },
                            room = generatedRoom?.id,
                            onSendFile = { files ->
                                viewModel.setReply(null)
                                viewModel.addMessage("", files)
                            },
                            showKeyboard = showKeyboard,
                            showEmojiKeyboard = showEmojiKeyboard,
                            onKeyboardRequest = { keyboard, emojiKeyboard ->
                                viewModel.showKeyboard.value = keyboard
                                viewModel.showEmojiKeyboard.value = emojiKeyboard
                            }, onSend = { text ->
                                viewModel.setReply(null)
                                viewModel.addMessage(text)
                            }) { text ->
                            viewModel.messageIdForReplyAndEdit.value = null
                            viewModel.editMessage(text)
                        }
                    }
                }
            }
        }, topBar = {
            Box {
                AppBarChat(user = user, status = status, {
                    navigation.popBackStack()
                }) {
                    navigation.navigate(
                        ChatPages.ProfileScreen.route + "?user=" + Gson().toJson(
                            user
                        )
                    )
                }
                AnimatedVisibility(
                    visible = selectedList.isNotEmpty(), enter = fadeIn(), exit = fadeOut()
                ) {
                    SmallTopAppBar(title = {
                        TextChangeAnimationCounter(
                            text = selectedList.size.toString(), style = LocalTextStyle.current
                        )
                    }, navigationIcon = {
                        IconButton(onClick = {
                            viewModel.selectedList.value = emptyList()
                        }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "close")
                        }
                    }, actions = {
                        IconButton(onClick = {
                            viewModel.copyMessages(selectedList)
                            viewModel.selectedList.value = emptyList()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_content_copy_24),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            viewModel.orderForward()
                            navigation.navigate(
                                ChatPages.ForwardScreen.route + "?messages" + Gson().toJson(
                                    selectedList
                                )
                            )
                            viewModel.selectedList.value = emptyList()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_forward),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            viewModel.deleteMessages(selectedList)
                            viewModel.selectedList.value = emptyList()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete, contentDescription = null
                            )
                        }
                    })
                }
            }
        }, floatingActionButton = {
            AnimatedVisibility(
                visible = lazyState.canScrollBackward,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                Card(
                    modifier = Modifier.size(48.dp), shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                scope.launch {
                                    lazyState.animateScrollToItem(0)
                                }
                            }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "down"
                        )
                    }
                }
            }
        }) { paddingValues ->
        MessagesList(
            modifier = Modifier.padding(paddingValues),
            lazyState = lazyState,
            message = messages,
            onContentClick = { offset, size, pair ->
                when (pair.second.type) {
                    Constants.GALLERY -> {
                        offsetChatMedia = offset
                        sizeChatMedia = size
                        popUpMedia = pair
                        showChatMedia = true
                    }

                    Constants.MUSIC -> {
                        viewModel.playOrPauseMusicWithCache(
                            pair.second, CurrentUser.token == pair.first.from, true
                        )
                    }
                }
            },
            playingMusic = currentMusic ?: Uri.EMPTY,
            currentPosition = currentTimePlayingAudio,
            selectedList = selectedList,
            onMessageEvent = {
                when (it) {
                    is MessageEvents.SeekExo -> {
                        viewModel.seekTo(it.position)
                    }

                    is MessageEvents.OpenUser -> {
                        navigation.navigate(
                            ChatPages.ProfileScreen.route + "?user=" + Gson().toJson(
                                it.user
                            )
                        )
                    }

                    is MessageEvents.Seen -> {
                        viewModel.seenMessage(it.id)
                    }

                    is MessageEvents.SetEmoji -> {
                        viewModel.setEmoji(it.messageModel, it.emoji)
                    }

                    is MessageEvents.Reply -> {
                        viewModel.setReply(it.id)
                    }

                    is MessageEvents.Click -> {
                        popUpChat = it.offset
                        selectedMessage = it.messageModel
                    }

                    is MessageEvents.OpenId -> {
                        viewModel.getUserWithId(it.id) { user ->
                            if (user != null) {
                                navigation.navigate(
                                    ChatPages.ProfileScreen.route + "?user=" + Gson().toJson(
                                        user
                                    )
                                )
                            } else {
                                scope.launch {
                                    snackBarState.showSnackbar(
                                        context.getString(R.string.user_not_found),
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                        }
                    }

                    is MessageEvents.LongClick -> {
                        try {
                            if (selectedList.contains(it.messageModel)) {
                                viewModel.selectedList.value = selectedList.toMutableList().apply {
                                    remove(it.messageModel)
                                }
                            } else {
                                viewModel.selectedList.value = selectedList.toMutableList().apply {
                                    add(it.messageModel)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is MessageEvents.CancelDownload -> {
                        viewModel.cancelDownload(it.path)
                    }

                    is MessageEvents.DownloadFile -> {
                        viewModel.downloadFile(it.path)
                    }
                }
            },
            to = user,
            downloadFiles = downloadFiles,
            uploadFiles = uploadFiles,
        )
    }
    MessagePopUp(offset = popUpChat,
        context,
        popUpChat != Offset.Zero,
        selectedMessage,
        myFrom = CurrentUser.token == currentRoom?.from?.token,
        onEvent = {
            when (it) {
                MessagePopUpEvent.REPLY -> {
                    viewModel.setReply(selectedMessage?.id ?: "")
                }

                MessagePopUpEvent.FORWARD -> {
                    val messageForReply = selectedMessage?.copy(
                        forwardFrom = if (selectedMessage?.from == CurrentUser.token) {
                            CurrentUser.user
                        } else {
                            user
                        }
                    )
                    navigation.navigate(
                        ChatPages.ForwardScreen.route + "?messages=" + Gson().toJson(
                            listOf(messageForReply)
                        )
                    )
                }

                MessagePopUpEvent.EDIT -> {
                    viewModel.messageIdForReplyAndEdit.value = Pair(true, selectedMessage?.id ?: "")
                }

                MessagePopUpEvent.COPY -> {
                    viewModel.copyMessages(listOf(selectedMessage ?: MessageModel()))
                }

                MessagePopUpEvent.DELETE -> {
                    if (selectedMessage?.id == replyId?.second) {
                        viewModel.messageIdForReplyAndEdit.value = null
                    }
                    viewModel.deleteMessages(listOf(selectedMessage ?: MessageModel()))
                }
            }
            selectedMessage = null
            popUpChat = Offset.Zero
        },
        onDismissRequest = {
            popUpChat = Offset.Zero
        }) {
        popUpChat = Offset.Zero
        selectedMessage = if (CurrentUser.token == currentRoom?.from?.token) {
            selectedMessage?.copy(fromEmoji = it)
        } else {
            selectedMessage?.copy(toEmoji = it)
        }
        viewModel.setEmoji(selectedMessage, it)
    }
    FileSelectorBottomSheet(
        show = filePopupShow,
        viewModel = viewModel,
        onDismissRequest = {
            filePopupShow = false
        }) { caption, files ->
        viewModel.addMessage(caption, files)
    }
    ChatMediaPopUp(
        show = showChatMedia,
        size = sizeChatMedia,
        offset = offsetChatMedia,
        message = popUpMedia?.first,
        file = popUpMedia?.second
    ) {
        showChatMedia = false
    }
    if (loading) {
        LoadingDialog()
    }
}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun TextFieldChat(
    selectedMessage: MessageModel?,
    isEdit: Boolean,
    user: UserModel,
    onReplyCancel: () -> Unit,
    onSendFile: (List<FileMessage>) -> Unit,
    onRecord: () -> Unit,
    room: String?,
    showEmojiKeyboard: Boolean,
    showKeyboard: Boolean,
    onKeyboardRequest: (Boolean, Boolean) -> Unit,
    onFileRequest: () -> Unit,
    onSend: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    val imeVisible = WindowInsets.isImeVisible
    val ime = WindowInsets.imeAnimationTarget
    val density = LocalDensity.current
    var heightKeyboard by remember {
        mutableFloatStateOf(with(density) { 300.dp.toPx() })
    }
    if (showEmojiKeyboard) {
        BackHandler {
            onKeyboardRequest.invoke(false, false)
        }
    }
    LaunchedEffect(key1 = imeVisible) {
        if (imeVisible) {
            heightKeyboard = ime.getBottom(density).toFloat()
        }
    }
    DisposableEffect(key1 = Unit) {
        onDispose { onKeyboardRequest.invoke(false, false) }
    }
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    Column(
        Modifier
            .background(surfaceColor)
            .navigationBarsPadding()
    ) {
        AnimatedVisibility(
            visible = selectedMessage != null,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            val name = if (selectedMessage?.from == CurrentUser.token) {
                CurrentUser.user?.getName() ?: ""
            } else user.getName()
            Column {
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            )
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = if (isEdit) R.drawable.round_edit_24 else R.drawable.round_reply_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = name,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = selectedMessage?.message ?: "",
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth(),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onReplyCancel) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                    }
                }
                Divider()
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom
        ) {
            Box(modifier = Modifier.height(64.dp), contentAlignment = Alignment.Center) {
                IconButton(onClick = {
                    onKeyboardRequest.invoke(false, showEmojiKeyboard.not())
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_insert_emoticon_24),
                        contentDescription = "emoji"
                    )
                }
            }
            StickerTextField(
                value = text,
                onValueChanged = {
                    text = it
                },
                placeHolder = stringResource(id = R.string.message),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .animateContentSize()
                    .heightIn(64.dp, 200.dp),
                showKeyboard = showKeyboard,
                onSendSticker = {
                    onSendFile.invoke(
                        listOf(
                            FileMessage(
                                it.toString(), it.toString(), type = Constants.STICKER
                            )
                        )
                    )
                },
                onFocusChanged = {
                    onKeyboardRequest.invoke(it, false)
                }, enabled = room != null
            )
            Box(modifier = Modifier.height(64.dp), contentAlignment = Alignment.Center) {
                AnimatedContent(targetState = when {
                    room == null -> 1
                    text.isEmpty() -> 2
                    isEdit -> 4
                    else -> 3
                }, label = "", transitionSpec = {
                    fadeIn() with fadeOut()
                }) {
                    when (it) {
                        1 -> {
                            CircularProgressIndicator(
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        2 -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    onKeyboardRequest.invoke(false, false)
                                    onFileRequest.invoke()
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_attach_file_24),
                                        contentDescription = "file",
                                        modifier = Modifier.rotate(45f)
                                    )
                                }
                                Surface(
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(40.0.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        1.dp
                                    ),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .combinedClickable(
                                                onLongClick = onRecord,
                                                onClick = {}),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.round_mic_none_24),
                                            contentDescription = "microphone",
                                        )
                                    }
                                }
                            }
                        }

                        4 -> {
                            FilledIconButton(onClick = {
                                onEdit.invoke(text)
                                text = ""
                                onKeyboardRequest.invoke(false, false)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_done_24),
                                    contentDescription = "send"
                                )
                            }
                        }

                        else -> {
                            IconButton(onClick = {
                                onSend.invoke(text)
                                text = ""
                                onKeyboardRequest.invoke(false, false)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_send_24),
                                    contentDescription = "send",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
        EmojiKeyboard(visible = showEmojiKeyboard && showKeyboard.not(),
            keyboardSize = heightKeyboard,
            placeHolder = stringResource(R.string.search),
            text = text,
            onDelete = {
                try {
                    text = text.removeRange(text.length.minus(1), text.length)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
            text += it
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarChat(
    user: UserModel,
    status: UserStatus?,
    onBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val appBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    val context = LocalContext.current
    SmallTopAppBar(title = {
        Row(modifier = Modifier
            .padding(start = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    onProfileClick.invoke()
                }
            }) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .placeholder(R.drawable.user_default)
                    .error(R.drawable.user_default)
                    .data(user.profilePictureUrl)
                    .crossfade(true)
                    .crossfade(500)
                    .build(),
                contentDescription = "profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = user.getName(),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
                Text(
                    text = status?.getText(context) ?: stringResource(id = R.string.connecting),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "back")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = appBarColor),
        actions = {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Rounded.Call, contentDescription = "call")
            }
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "more")
            }
        })
}