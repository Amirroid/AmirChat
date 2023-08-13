package ir.amirroid.amirchat.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ir.amirroid.amirchat.utils.getVideoFrames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TrimMediaProgressBar(
    path: String,
    duration: Long,
    position: Long,
    modifier: Modifier = Modifier,
    onProgressChanges: (Long) -> Unit
) {
    var sizeWidth by remember {
        mutableFloatStateOf(40f)
    }
    val frames = remember {
        mutableStateListOf<ImageBitmap>()
    }
    val x by animateFloatAsState(
        targetValue = (sizeWidth * (position.toFloat() / duration)).coerceIn(
            20f,
            sizeWidth.minus(20)
        ),
        label = ""
    )
    LaunchedEffect(key1 = path, key2 = duration) {
        withContext(Dispatchers.Default) {
            frames.clear()
            getVideoFrames(6, duration * 1000, path, frames)
        }
    }
    Canvas(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .height(64.dp)
            .border(2.dp, Color.White, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .pointerInteropFilter { event ->
                val present = event.x / sizeWidth
                onProgressChanges.invoke((duration * present).toLong())
                true
            }
    ) {
        sizeWidth = size.width
        val widthItem = size.width / frames.size
        frames.forEachIndexed { index, bitmap ->
            drawImage(
                bitmap,
                dstSize = IntSize(
                    widthItem.toInt(),
                    size.height.toInt()
                ),
                dstOffset = IntOffset(
                    widthItem.toInt() * index,
                    0
                )
            )
        }
        drawLine(
            Color.White,
            start = Offset(
                x,
                20f
            ),
            end = Offset(
                x,
                size.height - 20
            ),
            cap = StrokeCap.Round,
            strokeWidth = 10f
        )
    }
}