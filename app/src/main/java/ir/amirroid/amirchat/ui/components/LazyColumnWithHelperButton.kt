package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.utils.divIfNotZero
import ir.amirroid.amirchat.utils.toDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun LazyColumnWithHelperButton(
    count: Int,
    modifier: Modifier = Modifier.fillMaxSize(),
    context: Context = LocalContext.current,
    content: LazyListScope.() -> Unit
) {
    val lazyState = rememberLazyListState()
    var listSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val maxHeight = listSize.height.minus(50.toDp(context)).coerceAtLeast(1f)
    var offsetForButton by remember {
        mutableIntStateOf(0)
    }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val firstIndex by remember { derivedStateOf { lazyState.firstVisibleItemIndex } }
    var isDragging by remember {
        mutableStateOf(false)
    }
    val sizeItems by remember(count) {
        derivedStateOf {
            ((lazyState.layoutInfo.visibleItemsInfo.firstOrNull()?.size
                ?: 0) * count).divIfNotZero(maxHeight)
        }
    }
    LaunchedEffect(key1 = firstIndex) {
        if (isDragging.not()) {
            offsetForButton = (
                    ((lazyState.firstVisibleItemIndex.toFloat().div(count)) * maxHeight)
                    ).coerceIn(0f, maxHeight).toInt()
        }
        Log.d("refred", "LazyColumnWithHelperButton: $offsetForButton")
    }
    var showButton by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = lazyState.isScrollInProgress) {
        if (lazyState.isScrollInProgress.not()) delay(1000)
        showButton = lazyState.isScrollInProgress
    }
    Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            content = content,
            state = lazyState,
            modifier = modifier
                .onSizeChanged {
                    listSize = it
                })
        AnimatedVisibility(
            visible = showButton || isDragging,
            enter = fadeIn() + slideInHorizontally { 50.toDp(context).toInt() },
            exit = fadeOut() + slideOutHorizontally { 50.toDp(context).toInt() },
            modifier = Modifier.padding(top = with(density) { offsetForButton.toDp() })
        ) {
            Surface(
                shape = CircleShape.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                ),
                modifier = Modifier
                    .draggable(
                        rememberDraggableState {
                            scope.launch {
                                offsetForButton = offsetForButton
                                    .plus(it)
                                    .coerceIn(0f, maxHeight)
                                    .toInt()
                                val size =
                                    it * sizeItems
                                lazyState.scrollBy(size)
                            }
                        },
                        Orientation.Vertical,
                        onDragStarted = { isDragging = true },
                        onDragStopped = { isDragging = false })
                    .size(50.dp, 50.dp),
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_unfold_more_24),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}