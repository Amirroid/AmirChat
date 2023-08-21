package ir.amirroid.amirchat.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.net.toFile
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.formatTimeHourMinute
import ir.amirroid.amirchat.utils.getColorOfMessage
import ir.amirroid.amirchat.utils.getShapeOfMessage
import ir.amirroid.amirchat.utils.getTextColorOfMessage
import ir.amirroid.amirchat.utils.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MessageView(
    maxWidth: Dp,
    message: MessageModel,
    isMyUser: Boolean,
    replyEnabled: Boolean = true,
    onClick: (Offset) -> Unit,
    onLongClick: () -> Unit,
    onContentClick: ((Offset, Size, Pair<MessageModel, FileMessage>) -> Unit)? = null
) {
    SwipeBox(paddingEnd = if (isMyUser) 0.dp else 12.dp, enabled = replyEnabled) {
        MessageView(
            message = message,
            maxWidth = maxWidth,
            isMyUser,
            onClick,
            onLongClick,
            onContentClick
        )
    }
}


@Composable
fun RowScope.MessageView(
    message: MessageModel,
    maxWidth: Dp,
    isMyUser: Boolean,
    onClick: (Offset) -> Unit,
    onLongClick: () -> Unit,
    onContentClick: ((Offset, Size, Pair<MessageModel, FileMessage>) -> Unit)? = null
) {
    var position by remember {
        mutableStateOf(Offset.Zero)
    }
    Box(
        modifier = Modifier
            .weight(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick.invoke(position) },
                    onLongPress = { onLongClick.invoke() })
            },
        contentAlignment = if (isMyUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .offset(x = if (isMyUser) 18.dp else 0.dp)
                .widthIn(
                    max = maxWidth
                )
                .clip(
                    getShapeOfMessage(isMyUser)
                )
                .background(getColorOfMessage(isMyUser = isMyUser))
                .onPlaced {
                    position = it.positionInWindow()
                }
        ) {
            if (message.files.isNotEmpty()) {
                FilesContent(message) { offset, size, file ->
                    onContentClick?.invoke(offset, size, Pair(message, file))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (message.message.isNotEmpty()) {
                    SelectionContainer {
                        Text(
                            text = message.message,
                            modifier = Modifier,
                            color = getTextColorOfMessage(isMyUser = isMyUser)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .alpha(0.7f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.date.formatTimeHourMinute(),
                        style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurface)
                    )
                    val icon = when (message.status) {
                        Constants.SENDING -> R.drawable.outline_watch_later_24
                        Constants.SEND -> R.drawable.baseline_done_24
                        Constants.SEEN -> R.drawable.baseline_done_all_24
                        else -> R.drawable.outline_watch_later_24
                    }
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilesContent(files: MessageModel, onContentClick: ((Offset, Size, FileMessage) -> Unit)?) {
    when (files.files.first().type) {
        Constants.GALLERY -> {
            GalleryView(files, onContentClick)
        }
    }
}

@Composable
fun GalleryView(message: MessageModel, onContentClick: ((Offset, Size, FileMessage) -> Unit)?) {
    val files = message.files
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        if (files.size == 1) {
            var bitmap by remember {
                mutableStateOf<Bitmap?>(null)
            }
            var offset by remember {
                mutableStateOf(Offset.Zero)
            }
            var size by remember {
                mutableStateOf(Size.Zero)
            }
            LaunchedEffect(key1 = Unit) {
                if (message.from == CurrentUser.token) {
                    bitmap = BitmapFactory.decodeFile(files.first().fromPath)
                    ImageRequest.Builder(context).data(files.first().path)
                        .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                } else {
                    ImageRequest.Builder(context).data(files.first().path)
                        .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                }
            }
            AsyncImage(
                model = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable(enabled = onContentClick != null) {
                        onContentClick?.invoke(offset, size, files.first())
                    }
                    .onGloballyPositioned {
                        offset = it.positionInWindow()
                        size = it.size.toSize()
                    },
                contentScale = ContentScale.Crop
            )
        } else {
            Column {
                for (i in (0..files.size.plus(1).div(2).minus(1))) {
                    val index = i * 2
                    Row(modifier = Modifier.fillMaxWidth()) {
                        var offset by remember {
                            mutableStateOf(Offset.Zero)
                        }
                        var size by remember {
                            mutableStateOf(Size.Zero)
                        }
                        var bitmap by remember {
                            mutableStateOf<Bitmap?>(null)
                        }
                        var bitmap2 by remember {
                            mutableStateOf<Bitmap?>(null)
                        }
                        LaunchedEffect(key1 = Unit) {
                            if (message.from == CurrentUser.token) {
                                bitmap = BitmapFactory.decodeFile(files[index].fromPath)
                                ImageRequest.Builder(context).data(files[index].path)
                                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }
                                    .build()
                                if (files.size.minus(1) >= index.plus(1)) {
                                    bitmap2 = BitmapFactory.decodeFile(files[index.plus(1)].fromPath)
                                    ImageRequest.Builder(context)
                                        .data(files[index.plus(1)].path)
                                        .target { b ->
                                            bitmap2 = (b as BitmapDrawable).bitmap
                                        }
                                        .build()
                                }
                            } else {
                                ImageRequest.Builder(context).data(files[index].path)
                                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }
                                    .build()
                                if (files.size.minus(1) >= index.plus(1)) {
                                    ImageRequest.Builder(context)
                                        .data(files[index.plus(1)].path)
                                        .target { b ->
                                            bitmap2 = (b as BitmapDrawable).bitmap
                                        }
                                        .build()
                                }
                            }
                        }
                        AsyncImage(
                            model = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(max = 200.dp)
                                .clickable(enabled = onContentClick != null) {
                                    onContentClick?.invoke(offset, size, files[index])
                                }
                                .onGloballyPositioned {
                                    offset = it.positionInWindow()
                                    size = it.size.toSize()
                                },
                            contentScale = ContentScale.Crop
                        )
                        if (files.size.minus(1) >= index.plus(1)) {
                            var offset2 by remember {
                                mutableStateOf(Offset.Zero)
                            }
                            var size2 by remember {
                                mutableStateOf(Size.Zero)
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                            AsyncImage(
                                model = bitmap2,
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(max = 200.dp)
                                    .clickable(enabled = onContentClick != null) {
                                        onContentClick?.invoke(offset2, size2, files[index.plus(1)])
                                    }
                                    .onGloballyPositioned {
                                        offset2 = it.positionInWindow()
                                        size2 = it.size.toSize()
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    if (files.size >= index.plus(2)) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeBox(
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    paddingEnd: Dp,
    enabled: Boolean,
    content: @Composable RowScope.() -> Unit = {}
) {
    var offsetX = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val alphaReply by animateFloatAsState(
        targetValue = when {
            offsetX.value < -190 -> 1f
            offsetX.value > -10 -> 0f
            else -> .8f
        },
        label = ""
    )
    val hapticFeedback = LocalHapticFeedback.current
    var hapticFeedbackDone by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = offsetX.value) {
        if (offsetX.value < -190f && hapticFeedbackDone.not()) {
            hapticFeedbackDone = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            hapticFeedbackDone = false
        }
    }
    val colorReply = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    val colorReplyBorder = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val dragState = rememberDraggableState {
        scope.launch {
            offsetX.snapTo((offsetX.value + it.toInt()).coerceIn(-210f, 0f))
        }
    }
    Row(
        modifier = Modifier
            .offset {
                IntOffset(offsetX.value.toInt(), 0)
            }
            .fillMaxWidth()
            .draggable(
                dragState,
                Orientation.Horizontal,
                onDragStopped = {
                    scope.launch { offsetX.animateTo(0f, animationSpec = spring()) }
                },
                enabled = enabled
            )
            .padding(start = 12.dp, end = paddingEnd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content.invoke(this)
        Box(modifier = Modifier
            .size(30.dp)
            .drawWithContent {
                drawCircle(
                    colorReply.copy(alphaReply),
                )
                drawArc(
                    colorReplyBorder,
                    -90f,
                    offsetX.value.div(-200) * 360,
                    false,
                    style = Stroke(
                        2.toDp(context),
                        cap = StrokeCap.Round
                    ),
                    size = Size(
                        width = size.width - 3.toDp(context),
                        height = size.height - 3.toDp(context),
                    ),
                    topLeft = Offset(
                        x = 1.5f.toDp(context),
                        y = 1.5f.toDp(context),
                    )
                )
                drawContent()
            }
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = offsetX.value < -10,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                enter = scaleIn(animationSpec = spring()),
                exit = scaleOut(animationSpec = spring())
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_reply_24),
                    contentDescription = "reply",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}