package ir.amirroid.amirchat.ui.components

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ir.amirroid.amirchat.utils.getStatusBarHeight
import ir.amirroid.amirchat.utils.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    show: Boolean,
    title: String,
    initialSize: Float,
    context: Context,
    showToolbar: Boolean,
    scope: CoroutineScope,
    onDismissRequest: () -> Unit,
    enabled: Boolean = true,
    bottomBarContent: @Composable (Boolean) -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val displayMatrix = context.resources.displayMetrics
    val height = displayMatrix.heightPixels.toFloat()
    val heightModal = height * initialSize
    val currentOffset = remember {
        Animatable(height)
    }
    LaunchedEffect(key1 = initialSize) {
        if (show) currentOffset.snapTo(height - height * initialSize)
    }
    LaunchedEffect(key1 = show) {
        if (show) {
            currentOffset.animateTo(
                height - heightModal, spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow
                )
            )
        } else {
            currentOffset.animateTo(height)
        }
    }
    val radius by animateDpAsState(
        targetValue = if (currentOffset.value < 52.toDp(context)) 0.dp else 16.dp, label = ""
    )
    val dragState = rememberDraggableState {
        scope.launch {
            currentOffset.snapTo(
                currentOffset.value.plus(it).coerceIn(0f, height)
            )
        }
    }
    if (show) {
        BackHandler {
            if (currentOffset.value == 0f) {
                scope.launch {
                    currentOffset.animateTo(height - heightModal)
                }
            } else {
                onDismissRequest.invoke()
            }
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .then(if (show) Modifier.pointerInput(Unit) { detectTapGestures { onDismissRequest.invoke() } } else Modifier),
        contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        0.4f * (1 - currentOffset.value.coerceAtMost(
                            heightModal
                        ) / heightModal).coerceIn(0f, 1f)
                    )
                )
        )
        AnimatedVisibility(currentOffset.value != height,
            enter = slideInVertically { 200 },
            exit = slideOutVertically { 200 }) {
            Surface(
                shape = RoundedCornerShape(topStart = radius, topEnd = radius),
                modifier = Modifier
                    .draggable(dragState, Orientation.Vertical, onDragStopped = {
                        scope.launch {
                            when {
                                currentOffset.value > height - heightModal.div(2) -> {
                                    currentOffset.animateTo(height)
                                    onDismissRequest.invoke()
                                }

                                currentOffset.value < height - heightModal && currentOffset.value > (height - heightModal).div(
                                    2
                                ) -> {
                                    currentOffset.animateTo(height - heightModal)
                                }

                                currentOffset.value < (height - heightModal).div(2) -> {
                                    currentOffset.animateTo(0f)
                                }

                                else -> {
                                    currentOffset.animateTo(height - heightModal)
                                }
                            }
                        }
                    }, enabled = enabled)
                    .offset {
                        IntOffset(0, currentOffset.value.toInt())
                    }
                    .pointerInput(Unit) { detectTapGestures { } }
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(visible = currentOffset.value > 30 || showToolbar) {
                        BottomSheetDefaults.DragHandle()
                    }
                    AnimatedVisibility(visible = currentOffset.value < 30 && showToolbar.not()) {
                        Spacer(modifier = Modifier.height(
                            with(LocalDensity.current) { getStatusBarHeight(context = context).toDp() }
                        ))
                    }
                    AnimatedVisibility(
                        visible = currentOffset.value < 60.toDp(context) && showToolbar,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        content.invoke(this)
                    }
                }
            }
        }
        Box(
            modifier = Modifier.align(
                Alignment.BottomCenter
            ),
            contentAlignment = Alignment.BottomCenter
        ) {
            bottomBarContent.invoke(show && currentOffset.value != height && currentOffset.value != 0f)
        }
        AnimatedVisibility(
            visible = currentOffset.value < 60.toDp(context) && showToolbar,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            CenterAlignedTopAppBar(title = { Text(text = title) }, navigationIcon = {
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack, contentDescription = "back"
                    )
                }
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            )
            )
        }
    }
}