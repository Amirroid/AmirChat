package ir.amirroid.amirchat.ui.components

import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.utils.getColorOfMessage
import ir.amirroid.amirchat.utils.getShapeOfMessage
import ir.amirroid.amirchat.utils.getTextColorOfMessage
import ir.amirroid.amirchat.utils.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MessageView(
    maxWidth: Dp,
    text: String,
    isMyUser: Boolean,
    replyEnabled: Boolean = true,
    onClick: (Offset) -> Unit,
    onLongClick: () -> Unit,
    onContentClick: ((Offset, Size) -> Unit)? = null
) {
    SwipeBox(paddingEnd = if (isMyUser) 0.dp else 12.dp, enabled = replyEnabled) {
        MessageView(
            text = text,
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
    text: String,
    maxWidth: Dp,
    isMyUser: Boolean,
    onClick: (Offset) -> Unit,
    onLongClick: () -> Unit,
    onContentClick: ((Offset, Size) -> Unit)? = null
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomEnd = 4.dp,
                            bottomStart = 4.dp
                        )
                    )
            ) {
                var size by remember {
                    mutableStateOf(Size.Zero)
                }
                var offset by remember {
                    mutableStateOf(Offset.Zero)
                }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://www.alamto.com/wp-content/uploads/2023/05/flower-9.jpg")
                        .crossfade(true).crossfade(200).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onContentClick?.invoke(offset, size) }
                        .onGloballyPositioned {
                            size = it.size.toSize()
                            offset = it.positionInWindow()
                        }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = text,
                        modifier = Modifier,
                        color = getTextColorOfMessage(isMyUser = isMyUser)
                    )
                }
                Text(
                    text = "23:22",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End)
                        .alpha(0.7f),
                    style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurface)
                )
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