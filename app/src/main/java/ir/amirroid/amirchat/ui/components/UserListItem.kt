package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.utils.Profile
import ir.amirroid.amirchat.utils.formatDateTime
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.utils.toDp

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun UserListItem(
    room: ChatRoom,
    context: Context = LocalContext.current,
    density: Density,
    numbers: Int,
    selected: Boolean,
    selectionMode: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
    onLongClick: () -> Unit,
    onImageLongClick: () -> Unit,
) {
    val user = if (room.from.token == CurrentUser.token) {
        room.to
    } else room.from
    val dismissState = rememberDismissState(positionalThreshold = {
        it * .5f
    }, confirmValueChange = {
        if (it == DismissValue.DismissedToStart) {
            onDelete.invoke()
        }
        false
    })
    SwipeToDismiss(state = dismissState, background = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.error),
            contentAlignment = Alignment.CenterEnd
        ) {
            val progress by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) 1f else 0f,
                label = "",
                animationSpec = tween(500, easing = EaseInOut)
            )
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onError,
                label = "",
                animationSpec = tween(700)
            )
            val sizeIcon by animateDpAsState(
                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) 26.dp else 22.dp,
                label = "",
                animationSpec = spring()
            )
            val colorSurface = MaterialTheme.colorScheme.surfaceContainerHigh
            Canvas(modifier = Modifier.fillMaxSize()) {
                clipRect {
                    drawCircle(
                        colorSurface,
                        size.width * 2 * progress,
                        center = Offset(
                            size.width - 16.toDp(context) - with(density) {
                                sizeIcon.div(2).toPx()
                            },
                            size.height.div(2)
                        )
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "delete",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(sizeIcon),
                tint = color
            )
        }
    }, {
        val radius by animateDpAsState(
            targetValue = if (dismissState.dismissDirection == DismissDirection.EndToStart) 8.dp else 0.dp,
            label = "",
            animationSpec = tween(500, easing = EaseInOut)
        )
        ListItem(headlineContent = {
            Text(
                text = user.getName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }, leadingContent = {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(user.profilePictureUrl)
                        .allowHardware(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .placeholder(R.drawable.user_default)
                        .error(R.drawable.user_default)
                        .diskCachePolicy(CachePolicy.ENABLED).crossfade(true)
                        .crossfade(300).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = {
                                onImageLongClick.invoke()
                            })
                        },
                    contentScale = ContentScale.Crop
                )
                SelectionBox(checked = selected)
            }
        },
            supportingContent = if (room.lastMessage.isNotEmpty()) {
                {
                    Text(
                        text = room.lastMessage,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else null, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = radius, topEnd = radius))
                .combinedClickable(
                    onLongClick = onLongClick,
                    onClick = if (selectionMode) onLongClick else onClick,
                ),
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = room.lastTime.formatDateTime(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.alpha(0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    AnimatedContent(targetState = numbers, label = "", transitionSpec = {
                        scaleIn() with scaleOut()
                    }) {
                        if (it != 0) {
                            Badge(modifier = Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = it.toString()
                                )
                            }
                        }
                    }
                }
            }
        )
    }, directions = setOf(DismissDirection.EndToStart), modifier = modifier)
}