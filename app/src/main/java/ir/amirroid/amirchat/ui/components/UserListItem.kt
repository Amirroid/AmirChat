package ir.amirroid.amirchat.ui.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.utils.Profile
import ir.amirroid.amirchat.utils.toDp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserListItem(
    profile: Profile,
    context: Context = LocalContext.current,
    density: Density,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val dismissState = rememberDismissState(positionalThreshold = {
        it * .5f
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
        ListItem(headlineContent = { Text(text = profile.name) }, leadingContent = {
            AsyncImage(
                model = ImageRequest.Builder(context).data(profile.image).crossfade(true)
                    .crossfade(300).build(),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        },
            supportingContent = {
                Text(text = profile.desc)
            }, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = radius, topEnd = radius))
                .combinedClickable(
                    onLongClick = onLongClick,
                    onClick = onClick,
                ),
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "16:41",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Badge(modifier = Modifier.padding(top = 4.dp)) {
                        Text(text = "16")
                    }
                }
            }
        )
    }, directions = setOf(DismissDirection.EndToStart))
}