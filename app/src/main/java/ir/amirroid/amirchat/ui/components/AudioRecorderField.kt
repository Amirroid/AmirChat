package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.utils.divIfNotZero
import ir.amirroid.amirchat.utils.formatTime
import ir.amirroid.amirchat.utils.getMilliseconds
import ir.amirroid.amirchat.utils.toDp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun AudioRecorderField(
    timeRecording: Long,
    onCancel: () -> Unit,
    onStopRequest: (shwoPreview: Boolean) -> Unit,
) {
    val mis = remember {
        MutableInteractionSource()
    }
    val context = LocalContext.current
    val displayMatrix = context.resources.displayMetrics
    val width = displayMatrix.widthPixels
    val scope = rememberCoroutineScope()
    val offsetXButton = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = Unit) {
        mis.emit(DragInteraction.Start())
    }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val colorRecording by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        targetValue = Color.Red,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val textMovableAnimation by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val microphoneBitmap = remember {
        ContextCompat.getDrawable(context, R.drawable.round_mic_none_24)?.toBitmap()
            ?.asImageBitmap()
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(56.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp)
            ) {
                AnimatedContent(
                    targetState = offsetXButton.value > width.div(4),
                    transitionSpec = { fadeIn() with fadeOut() },
                    label = ""
                ) {
                    if (it) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(20.dp)
                                .background(colorRecording)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextChangeAnimation(
                    text = timeRecording.formatTime(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = " ," + timeRecording.getMilliseconds(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = stringResource(id = R.string.slide_to_cancel), modifier = Modifier
                    .align(
                        Alignment.Center
                    )
                    .offset {
                        IntOffset(
                            (-offsetXButton.value * 0.7f).toInt() + textMovableAnimation.toInt(),
                            0
                        )
                    }
                    .alpha(
                        1 - offsetXButton.value
                            .divIfNotZero(width.div(2))
                            .coerceIn(0f, 1f)
                    )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterEnd),
            horizontalAlignment = Alignment.End
        ) {
            Surface(
                modifier = Modifier
                    .padding(end = 22.dp)
                    .size(36.dp),
                shape = CircleShape,
                shadowElevation = 1.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onStopRequest.invoke(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id =
                            R.drawable.round_stop_24
                        ),
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(92.dp)
                    .indication(mis, null)
                    .draggable(rememberDraggableState {
                        scope.launch {
                            offsetXButton.snapTo(
                                offsetXButton.value
                                    .minus(it)
                                    .coerceIn(0f, width.div(2f))
                            )
                        }
                    }, Orientation.Horizontal, onDragStopped = {
                        if (offsetXButton.value > width.div(4)) {
                            onCancel.invoke()
                        }
                        offsetXButton.animateTo(0f)
                    })
                    .pointerInput(Unit) {
                        detectTapGestures {
                            if (it.x < (width * 0.6f).minus(offsetXButton.value) && it.x > (width * 0.6f)
                                    .minus(
                                        offsetXButton.value
                                    )
                                    .minus(92.toDp(context))
                            ) {
                                onStopRequest.invoke(false)
                            }
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val radius = height.div(2)
                drawCircle(
                    primaryColor,
                    radius = radius,
                    center = Offset(
                        x = width - radius.div(2) - offsetXButton.value,
                        y = center.y
                    )
                )
                if (microphoneBitmap != null) {
                    drawImage(
                        microphoneBitmap,
                        topLeft = Offset(
                            width - radius.div(2) - offsetXButton.value - microphoneBitmap.width.div(
                                2
                            ),
                            center.y - microphoneBitmap.height.div(2)
                        ),
                        colorFilter = ColorFilter.tint(onPrimaryColor, BlendMode.SrcIn)
                    )
                }
            }
        }
    }
}