package ir.amirroid.amirchat.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import coil.compose.AsyncImage
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.ui.features.chat.AppBarChat
import ir.amirroid.amirchat.utils.SimpleList


@Composable
fun ChatPopUp(
    visible: Boolean,
    onDismissRequest: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.9f,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow,
            )
        ) + fadeIn(),
        exit = scaleOut(
            targetScale = 0.9f,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        onDismissRequest.invoke()
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
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.reply)) },
                            onClick = { },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_reply_24),
                                    contentDescription = "reply"
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.copy)) },
                            onClick = { },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                                    contentDescription = "copy"
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.forward)) },
                            onClick = { },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_reply_24),
                                    contentDescription = "forward",
                                    modifier = Modifier.rotate(180f)
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.save)) },
                            onClick = { },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_bookmark_border_24),
                                    contentDescription = "save"
                                )
                            })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete)) },
                            onClick = { },
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ChatSmallToolbar {}
        MessagesList(messages = SimpleList.listMessages, showPattern = true, replyEnabled = false, onClick = {}) {

        }
    }
}

@Composable
fun ChatSmallToolbar(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .pointerInput(Unit) {
                detectTapGestures { onClick }
            }
    ) {
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