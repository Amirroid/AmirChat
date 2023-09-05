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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
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
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.events.MessageEvents
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.ui.components.AudioRecordedPreview
import ir.amirroid.amirchat.ui.components.AudioRecorderField
import ir.amirroid.amirchat.ui.components.ChatMediaPopUp
import ir.amirroid.amirchat.ui.components.FileSelectorBottomSheet
import ir.amirroid.amirchat.ui.components.MessagePopUp
import ir.amirroid.amirchat.ui.components.MessagesList
import ir.amirroid.amirchat.ui.components.StickerTextField
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.viewmodels.ChatViewModel
import ir.amirroid.emojikeyboard2.EmojiKeyboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalAnimationApi::class)
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
    val replyId by viewModel.reply.collectAsStateWithLifecycle()
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
    DisposableEffect(key1 = Unit) {
        viewModel.observeToChats(room, user)
        onDispose { focusManager.clearFocus() }
    }
    LaunchedEffect(key1 = messages.size) {
        delay(200)
        lazyState.animateScrollToItem(messages.size)
    }
    Scaffold(bottomBar = {
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
                    onDelete = { viewModel.cancelRecording() })
            } else {
                if (isRecording) {
                    AudioRecorderField(timeRecording = currentTimeAudio, onCancel = {
                        viewModel.cancelRecording()
                    }) {
                        viewModel.stopRecording(it)
                    }
                } else {
                    TextFieldChat(
                        if (replyId == null) null else messages.firstOrNull { user -> user.id == replyId },
                        user,
                        onReplyCancel = {
                            viewModel.reply.value = null
                        },
                        onRecord = { viewModel.requestRecord() }, onFileRequest = {
                            viewModel.getMedias()
                            filePopupShow = true
                        }, onSendFile = { files ->
                            viewModel.reply.value = null
                            viewModel.setMessages(
                                messages.toMutableList().apply {
                                    add(
                                        MessageModel(
                                            "",
                                            from = CurrentUser.token ?: "",
                                            replyToId = replyId,
                                            files = files
                                        )
                                    )
                                }
                            )
                            viewModel.addMessage("", files)
                        }) { text ->
                        viewModel.reply.value = null
                        viewModel.setMessages(
                            messages.toMutableList().apply {
                                add(
                                    MessageModel(
                                        text,
                                        from = CurrentUser.token ?: "",
                                        replyToId = replyId
                                    )
                                )
                            }
                        )
                        viewModel.addMessage(text)
                    }
                }
            }
        }
    }, topBar = {
        AppBarChat(user = user, {
            navigation.popBackStack()
        }) {
            navigation.navigate(ChatPages.ProfileScreen.route)
        }
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = lazyState.canScrollForward,
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
                                lazyState.animateScrollToItem(messages.size)
                            }
                        }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = "down"
                    )
                }
            }
        }
    }) { paddingValues ->
        MessagesList(
            modifier = Modifier.padding(paddingValues),
            lazyState = lazyState,
            messages = messages,
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
                            pair.second,
                            CurrentUser.token == pair.first.from,
                            true
                        )
                    }
                }
            }, playingMusic = currentMusic ?: Uri.EMPTY,
            currentPosition = currentTimePlayingAudio,
            selectedList = selectedList,
            onMessageEvent = {
                when (it) {
                    is MessageEvents.SeekExo -> {
                        viewModel.seekTo(it.position)
                    }

                    is MessageEvents.SetEmoji -> {
                        viewModel.setEmoji(it.messageModel, it.emoji)
                    }

                    is MessageEvents.Reply -> {
                        viewModel.reply.value = it.id
                    }

                    is MessageEvents.Click -> {
                        popUpChat = it.offset
                        selectedMessage = it.messageModel
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
                }
            },
            to = user
        )
    }
    MessagePopUp(
        offset = popUpChat,
        context,
        popUpChat != Offset.Zero,
        selectedMessage,
        myFrom = CurrentUser.token == currentRoom?.from?.token,
        onDismissRequest = {
            popUpChat = Offset.Zero
        }) {
        popUpChat = Offset.Zero
        selectedMessage = if (CurrentUser.token == currentRoom?.from?.token){
            selectedMessage?.copy(fromEmoji = it)
        }else{
            selectedMessage?.copy(toEmoji = it)
        }
        viewModel.setEmoji(selectedMessage, it)
    }
    FileSelectorBottomSheet(show = filePopupShow, viewModel = viewModel, onDismissRequest = {
        filePopupShow = false
    }) { caption, files ->
        viewModel.setMessages(
            messages.toMutableList().apply {
                add(
                    MessageModel(
                        caption,
                        files,
                        from = CurrentUser.token ?: "",
                        replyToId = replyId
                    )
                )
            }
        )
        viewModel.addMessage(caption, files)
    }
    ChatMediaPopUp(
        show = showChatMedia,
        size = sizeChatMedia,
        offset = offsetChatMedia,
        message = popUpMedia?.first?.message ?: "",
        file = popUpMedia?.second
    ) {
        showChatMedia = false
    }
}

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class
)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun TextFieldChat(
    replyMessage: MessageModel?,
    user: UserModel,
    onReplyCancel: () -> Unit,
    onSendFile: (List<FileMessage>) -> Unit,
    onRecord: () -> Unit, onFileRequest: () -> Unit, onSend: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    var showKeyboard by remember {
        mutableStateOf(true)
    }
    var showEmojiKeyboard by remember {
        mutableStateOf(false)
    }
    val imeVisible = WindowInsets.isImeVisible
    val ime = WindowInsets.imeAnimationTarget
    val density = LocalDensity.current
    var heightKeyboard by remember {
        mutableFloatStateOf(ime.getBottom(density).toFloat())
    }
    if (showEmojiKeyboard) {
        BackHandler {
            showEmojiKeyboard = false
        }
    }
    LaunchedEffect(key1 = imeVisible) {
        if (imeVisible) {
            heightKeyboard = ime.getBottom(density).toFloat()
        }
    }
    DisposableEffect(key1 = Unit) {
        onDispose { showKeyboard = false }
    }
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    Column(
        Modifier
            .background(surfaceColor)
            .navigationBarsPadding()
    ) {
        AnimatedVisibility(
            visible = replyMessage != null,
            enter = expandVertically(expandFrom = Alignment.Bottom),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            val name = if (replyMessage?.from == CurrentUser.token) {
                CurrentUser.user?.getName() ?: ""
            } else user.getName()
            Column {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_reply_24),
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
                            text = replyMessage?.message ?: "",
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth(),
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
                    showKeyboard = false
                    showEmojiKeyboard = showEmojiKeyboard.not()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_insert_emoticon_24),
                        contentDescription = "emoji"
                    )
                }
            }
            StickerTextField(value = text,
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
                                it.toString(),
                                it.toString(),
                                type = Constants.STICKER
                            )
                        )
                    )
                },
                onFocusChanged = {
                    showKeyboard = it
                    if (it) showEmojiKeyboard = false
                })
            Box(modifier = Modifier.height(64.dp), contentAlignment = Alignment.Center) {
                AnimatedContent(targetState = text.isEmpty(), label = "", transitionSpec = {
                    fadeIn() with fadeOut()
                }) {
                    if (it) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                showKeyboard = false
                                showEmojiKeyboard = false
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
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .combinedClickable(onLongClick = onRecord, onClick = {}),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_mic_none_24),
                                        contentDescription = "microphone",
                                    )
                                }
                            }
                        }
                    } else {
                        IconButton(onClick = {
                            onSend.invoke(text)
                            text = ""
                            showKeyboard = false
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
fun AppBarChat(user: UserModel, onBack: () -> Unit, onProfileClick: () -> Unit) {
    val appBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    SmallTopAppBar(title = {
        Row(modifier = Modifier
            .padding(start = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    onProfileClick.invoke()
                }
            }) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = user.getName(),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
                Text(
                    text = "Connecting...",
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