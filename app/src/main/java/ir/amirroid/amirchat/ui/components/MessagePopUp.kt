package ir.amirroid.amirchat.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.toDp
import ir.amirroid.amirchat.utils.toIntOffset

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MessagePopUp(
    offset: Offset,
    context: Context,
    visible: Boolean,
    message: MessageModel?,
    myFrom: Boolean,
    onEvent: (MessagePopUpEvent) -> Unit,
    onDismissRequest: () -> Unit,
    onSelect: (String?) -> Unit
) {
    val height = context.resources.displayMetrics.heightPixels
    val dpPadding = 78.toDp(context)
    val emoji = Constants.listMessageEmoji
    var offsetForView by remember {
        mutableStateOf(IntOffset.Zero)
    }
    var sizeView by remember {
        mutableStateOf(IntSize.Zero)
    }
    var selectedMessage by remember {
        mutableStateOf<MessageModel?>(null)
    }
    LaunchedEffect(key1 = message) {
        if (message != null) {
            selectedMessage = message
        }
    }
    LaunchedEffect(key1 = offset, key2 = sizeView) {
        if (offset != Offset.Zero) {
            val newOffset =
                offset.toIntOffset().copy(x = 100).minus(IntOffset(0, dpPadding.toInt()))
            offsetForView = if (newOffset.y + sizeView.height > height - 100) {
                newOffset.copy(y = height - 100 - sizeView.height)
            } else {
                newOffset.copy(y = newOffset.y.coerceAtLeast(100))
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.5f))
                    .pointerInput(Unit) {
                        detectTapGestures { onDismissRequest.invoke() }
                    }
            )
        }
        Column(
            modifier = Modifier
                .onSizeChanged {
                    sizeView = it
                }
                .offset {
                    offsetForView
                },
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(initialScale = 0.6f) + fadeIn(),
                exit = scaleOut(targetScale = 0.6f) + fadeOut(),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            1.dp
                        )
                    )
                ) {
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        items(emoji.size) {
                            val thisEmoji = emoji[it]
                            val selected = if (myFrom) {
                                selectedMessage?.fromEmoji == thisEmoji
                            } else {
                                selectedMessage?.toEmoji == thisEmoji
                            }
                            AnimatedVisibility(
                                visible = true,
                                initiallyVisible = false,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ) {
                                IconButton(
                                    onClick = {
                                        onSelect.invoke(if (selected) null else thisEmoji)
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(
                                            0.6f
                                        ) else Color.Transparent
                                    )
                                ) {
                                    Text(text = thisEmoji, fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(initialScale = 0.6f) + expandVertically(expandFrom = Alignment.Top),
                exit = scaleOut(targetScale = 0.6f) + shrinkVertically(shrinkTowards = Alignment.Top),
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(0.6f)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                ) {
                    val isMyUser = selectedMessage?.from == CurrentUser.token
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.reply)) },
                        onClick = { onEvent.invoke(MessagePopUpEvent.REPLY) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.round_reply_24),
                                contentDescription = "reply"
                            )
                        })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.copy)) },
                        onClick = { onEvent.invoke(MessagePopUpEvent.COPY) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_content_copy_24),
                                contentDescription = "copy"
                            )
                        })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.forward)) },
                        onClick = { onEvent.invoke(MessagePopUpEvent.FORWARD) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_forward),
                                contentDescription = "forward",
                            )
                        })
                    if (isMyUser) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.edit)) },
                            onClick = {
                                onEvent.invoke(MessagePopUpEvent.EDIT)
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_edit_24),
                                    contentDescription = "edit"
                                )
                            })
                    }
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.save)) },
                        onClick = { onEvent.invoke(MessagePopUpEvent.SAVE) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.round_bookmark_border_24),
                                contentDescription = "save"
                            )
                        })
                    if (isMyUser) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete)) },
                            onClick = { onEvent.invoke(MessagePopUpEvent.DELETE) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "delete"
                                )
                            })
                    }
                }
            }
        }
    }
}

enum class MessagePopUpEvent {
    DELETE,
    COPY,
    REPLY,
    EDIT,
    FORWARD,
    SAVE
}