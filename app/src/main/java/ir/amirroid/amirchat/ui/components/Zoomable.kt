package ir.amirroid.amirchat.ui.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext

@Composable
fun Zoomable(content: @Composable () -> Unit) {
    var zoom by remember {
        mutableFloatStateOf(1f)
    }
    var rotation by remember {
        mutableFloatStateOf(0f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    val context = LocalContext.current
    val displayMatrix = context.resources.displayMetrics
    Box(
        modifier = Modifier
            .graphicsLayer {
                rotationZ = rotation
                scaleX = zoom
                scaleY = zoom
                translationX = offset.x
                translationY = offset.y
            }
    ) {
        content.invoke()
    }
}