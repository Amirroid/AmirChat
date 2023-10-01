package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.ui.features.chat.AppBarChat
import ir.amirroid.amirchat.utils.SimpleList
import ir.amirroid.amirchat.viewmodels.ChatViewModel


@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun ChatPopUp(
    visible: Boolean, room: ChatRoom, onDismissRequest: (Int) -> Unit
) {
    AnimatedVisibility(
        visible = visible, enter = scaleIn(
            initialScale = 0.9f, animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow,
            )
        ) + fadeIn(), exit = scaleOut(
            targetScale = 0.9f, animationSpec = spring(
                dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        onDismissRequest.invoke(-1)
                    }
                }, contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .fillMaxHeight(0.88f)
            ) {
                ChatUi()
                Card(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(0.6f),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            1.dp
                        )
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column {
                        DropdownMenuItem(text = { Text(text = stringResource(id = R.string.delete_messages)) },
                            onClick = { onDismissRequest.invoke(1) },
                            leadingIcon = {
                                Crossfade(targetState = room.toNotificationEnabled(), label = "") {
                                    Icon(
                                        painter = painterResource(
                                            R.drawable.outline_delete_sweep_24
                                        ),
                                        contentDescription = "delete messages"
                                    )
                                }
                            })
                        DropdownMenuItem(text = { Text(text = stringResource(id = if (room.myNotificationEnabled()) R.string.mute else R.string.unmute)) },
                            onClick = { onDismissRequest.invoke(2) },
                            leadingIcon = {
                                Crossfade(targetState = room.toNotificationEnabled(), label = "") {
                                    Icon(
                                        painter = painterResource(
                                            id =
                                            if (room.myNotificationEnabled()) R.drawable.outline_notifications_off_24 else R.drawable.baseline_notifications_none_24
                                        ),
                                        contentDescription = "notification"
                                    )
                                }
                            })
                        DropdownMenuItem(text = { Text(text = stringResource(id = R.string.mark_as_read)) },
                            onClick = { onDismissRequest.invoke(3) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_done_all_24),
                                    contentDescription = "read"
                                )
                            })
                        DropdownMenuItem(text = { Text(text = stringResource(id = R.string.delete)) },
                            onClick = { onDismissRequest.invoke(4) },
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


@Composable
fun ColumnScope.ChatUi() {
    val downloadFiles by ChatViewModel.downloadFiles.collectAsStateWithLifecycle()
    val uploadFiles by ChatViewModel.uploadFiles.collectAsStateWithLifecycle()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ChatSmallToolbar {}
        MessagesList(
            message = emptyList(),
            showPattern = true,
            replyEnabled = false,
            to = UserModel(),
            downloadFiles = downloadFiles,
            uploadFiles = uploadFiles

        )
    }
}

@Composable
fun ChatSmallToolbar(onClick: () -> Unit) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .pointerInput(Unit) {
            detectTapGestures { onClick }
        }) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://www.alamto.com/wp-content/uploads/2023/05/flower-9.jpg",
                contentDescription = "profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Amirreza",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
                Text(
                    text = "Connecting...",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}