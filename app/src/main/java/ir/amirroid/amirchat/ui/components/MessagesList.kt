package ir.amirroid.amirchat.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.events.MessageEvents
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun MessagesList(
    lazyState: LazyListState = rememberLazyListState(),
    messages: List<MessageModel>,
    modifier: Modifier = Modifier,
    showPattern: Boolean = true,
    replyEnabled: Boolean = true,
    to: UserModel,
    onContentClick: ((Offset, Size, Pair<MessageModel, FileMessage>) -> Unit)? = null,
    onMessageEvent: ((MessageEvents) -> Unit)? = null,
    currentPosition: Long? = null,
    playingMusic: Uri = Uri.EMPTY,
    selectedList: List<MessageModel> = emptyList(),
) {
    val context = LocalContext.current
    val configuration = context.resources.configuration
    val maxWidth = (configuration.screenWidthDp * 0.7f).dp
    val animatedList = remember {
        mutableStateListOf<MessageModel>()
    }
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
        LazyColumn(
            modifier = Modifier
                .then(modifier)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.Bottom,
            state = lazyState,
        ) {
            items(messages.size) {
                val message = messages[it]
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically { 50 },
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically { 50 },
                    initiallyVisible = animatedList.contains(message),
                    modifier = Modifier.animateItemPlacement()
                ) {
                    MessageView(
                        maxWidth,
                        message,
                        message.from == CurrentUser.token,
                        replyEnabled,
                        playingMusic,
                        onMessageEvent = onMessageEvent,
                        currentPosition = if (message.files.firstOrNull()?.type == Constants.MUSIC) currentPosition else null,
                        onContentClick = onContentClick,
                        if (message.replyToId == null) null else messages.firstOrNull { user -> user.id == message.replyToId },
                        selectedList.contains(message),
                        to,
                        selectedList.isNotEmpty()
                    )
//                    if (animatedList.contains(message).not()) {
//                        LaunchedEffect(Unit) {
//                            delay(300)
//                            animatedList.add(message)
//                        }
//                    }
                    animatedList.add(message)
                }
                if (it != messages.size.minus(1)) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}