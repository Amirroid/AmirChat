package ir.amirroid.amirchat.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.events.MessageEvents
import ir.amirroid.amirchat.data.helpers.FileNetData
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.CircleShape
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.formatDate
import ir.amirroid.amirchat.utils.formatDateTimeForFile
import ir.amirroid.amirchat.utils.startLongPress
import kotlinx.coroutines.delay

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun MessagesList(
    lazyState: LazyListState = rememberLazyListState(),
    message: List<MessageModel>,
    modifier: Modifier = Modifier,
    showPattern: Boolean = true,
    replyEnabled: Boolean = true,
    to: UserModel,
    onContentClick: ((Offset, Size, Pair<MessageModel, FileMessage>) -> Unit)? = null,
    onMessageEvent: ((MessageEvents) -> Unit)? = null,
    currentPosition: Long? = null,
    playingMusic: Uri = Uri.EMPTY,
    selectedList: List<MessageModel> = emptyList(),
    downloadFiles: HashMap<String, FileNetData>,
    uploadFiles: HashMap<String, FileNetData>,
) {
    val context = LocalContext.current
    val configuration = context.resources.configuration
    val maxWidth = (configuration.screenWidthDp * 0.7f).dp
    var groupedMessage by remember {
        mutableStateOf<Map<String, List<MessageModel>>>(emptyMap())
    }
    var firstCommit by remember {
        mutableStateOf(true)
    }
    val hapticFeedback = LocalHapticFeedback.current
    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(key1 = message) {
        val canForward = lazyState.canScrollForward
        groupedMessage = message.groupBy {
            it.date.formatDate()
        }
        if (canForward.not() || imeVisible) {
            lazyState.animateScrollToItem(message.size)
        }
        if (firstCommit) {
            delay(200)
            lazyState.scrollToItem(message.size)
            firstCommit = false
        }
        hapticFeedback.startLongPress()
    }
    var showStickyHeader by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = lazyState.isScrollInProgress) {
        showStickyHeader = if (lazyState.isScrollInProgress){
            true
        }else{
            delay(3000)
            false
        }
    }
    val alphaForStickyHeader by animateFloatAsState(
        targetValue = if (showStickyHeader) 1f else 0f,
        label = ""
    )
    Box {
        if (showPattern) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(R.drawable.wallpaper).crossfade(true)
                    .crossfade(500).build(), contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.05f),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.primary,
                    BlendMode.SrcIn
                )
            )
        }
        LazyColumnFunc(
            modifier = modifier,
            lazyState = lazyState
        ) {
            groupedMessage.forEach { (date, gMessages) ->
                stickyHeader(key = date) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .alpha(alphaForStickyHeader),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(0.25f))
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(text = date)
                        }
                    }
                }
                items(gMessages.size, key = {
                    gMessages[it].id
                }) {
                    val iMessage = gMessages[it]
                    Box(modifier = Modifier.animateItemPlacement()) {
                        MessageView(
                            maxWidth,
                            iMessage,
                            iMessage.isMyMessage(),
                            replyEnabled,
                            playingMusic,
                            onMessageEvent = onMessageEvent,
                            currentPosition = if (iMessage.files.firstOrNull()?.type == Constants.MUSIC) currentPosition else null,
                            onContentClick = onContentClick,
                            if (iMessage.replyToId == null) null else gMessages.firstOrNull { user -> user.id == iMessage.replyToId },
                            selectedList.contains(iMessage),
                            to,
                            selectedList.isNotEmpty(),
                            downloadFiles,
                            uploadFiles
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun LazyColumnFunc(
    modifier: Modifier,
    lazyState: LazyListState,
    content: (LazyListScope.() -> Unit)
) {
    LazyColumn(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.Bottom,
        state = lazyState, content = content
    )
}