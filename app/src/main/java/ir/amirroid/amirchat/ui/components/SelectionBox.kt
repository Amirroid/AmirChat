package ir.amirroid.amirchat.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Path
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text2.input.TextFieldState.Saver.save
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.utils.getColor
import ir.amirroid.amirchat.utils.toDp
import kotlinx.coroutines.launch

@Composable
fun SelectionBox(
    checked: Boolean,
    context: Context = LocalContext.current
) {
    var completeSize by remember {
        mutableFloatStateOf(30.toDp(context))
    }
    val radiusMask = remember {
        Animatable(30.toDp(context))
    }
    val scope = rememberCoroutineScope()
    DisposableEffect(key1 = completeSize) {
        scope.launch {
            if (checked) {
                radiusMask.snapTo(0f)
            } else {
                radiusMask.snapTo(completeSize)
            }
        }
        onDispose { }
    }
    LaunchedEffect(key1 = checked, completeSize) {
        if (checked) {
            radiusMask.animateTo(0f, tween(300))
        } else {
            radiusMask.animateTo(completeSize, tween(300))
        }
    }
    val backgroundColor = MaterialTheme.colorScheme.background
    val bitmapCheck = remember {
        ContextCompat.getDrawable(context, R.drawable.round_check_24)?.toBitmap()?.asImageBitmap()
    }
    val sizeMinute = 6.toDp(context).toInt()
    Canvas(modifier = Modifier.size(24.dp)) {
        val sizePx = size.width
        completeSize = sizePx.div(2)
        val path = Path()
        path.addCircle(center.x, center.y, completeSize, Path.Direction.CW)
        path.addCircle(center.x, center.y, radiusMask.value, Path.Direction.CW)
        path.fillType = Path.FillType.EVEN_ODD
        clipPath(path.asComposePath()) {
            drawCircle(backgroundColor)
            drawCircle(
                getColor("#279144"),
                radius = completeSize.minus(6)
            )
            if (bitmapCheck != null) {
                drawImage(
                    bitmapCheck,
                    dstSize = IntSize(sizePx.toInt() - sizeMinute, sizePx.toInt() - sizeMinute),
                    dstOffset = IntOffset(sizeMinute.div(2), sizeMinute.div(2)),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}

@Composable
fun SelectionButton(
    checked: Boolean,
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier
) {
    var completeSize by remember {
        mutableFloatStateOf(30.toDp(context))
    }
    val radiusMask = remember {
        Animatable(completeSize)
    }
    LaunchedEffect(key1 = checked) {
        if (checked) {
            radiusMask.animateTo(0f, tween(300))
        } else {
            radiusMask.animateTo(completeSize, tween(300))
        }
    }
    val bitmapCheck = remember {
        ContextCompat.getDrawable(context, R.drawable.round_check_24)?.toBitmap()?.asImageBitmap()
    }
    val sizeMinute = 6.toDp(context).toInt()
    Canvas(
        modifier = Modifier
            .defaultMinSize(24.dp)
            .border(2.dp, Color.White, CircleShape)
            .clip(CircleShape)
            .then(
                modifier
            )
    ) {
        val sizePx = size.width
        completeSize = sizePx.div(2)
        val path = Path()
        path.addCircle(center.x, center.y, completeSize, Path.Direction.CW)
        path.addCircle(center.x, center.y, radiusMask.value, Path.Direction.CW)
        path.fillType = Path.FillType.EVEN_ODD
        clipPath(path.asComposePath()) {
            drawCircle(
                getColor("#279144"),
                radius = completeSize.minus(6)
            )
            if (bitmapCheck != null) {
                drawImage(
                    bitmapCheck,
                    dstSize = IntSize(sizePx.toInt() - sizeMinute, sizePx.toInt() - sizeMinute),
                    dstOffset = IntOffset(sizeMinute.div(2), sizeMinute.div(2)),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}

@Composable
fun SelectionNumberButton(
    checked: Boolean,
    text: String,
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    var completeSize by remember {
        mutableFloatStateOf(30.toDp(context))
    }
    val radiusMask = remember {
        Animatable(30.toDp(context))
    }
    val scope = rememberCoroutineScope()
    DisposableEffect(key1 = completeSize) {
        scope.launch {
            if (checked) {
                radiusMask.snapTo(0f)
            } else {
                radiusMask.snapTo(completeSize)
            }
        }
        onDispose { }
    }
    LaunchedEffect(key1 = checked, completeSize) {
        if (checked) {
            radiusMask.animateTo(0f, tween(300))
        } else {
            radiusMask.animateTo(completeSize, tween(300))
        }
    }
    var showText by remember {
        mutableStateOf("1")
    }
    LaunchedEffect(key1 = text) {
        if (text != "0") {
            showText = text
        }
    }
    val textColor = Color.White
    val textStyle = TextStyle.Default.copy(color = textColor, fontWeight = FontWeight.Bold)
    val measurable = rememberTextMeasurer()
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape)
    ) {
        Canvas(modifier = Modifier
            .size(24.dp)
            .toggleable(checked) {
                onCheckedChange.invoke(it)
            }) {
            val sizePx = size.width
            completeSize = sizePx.div(2)
            val path = Path()
            path.addCircle(center.x, center.y, completeSize, Path.Direction.CW)
            path.addCircle(center.x, center.y, radiusMask.value, Path.Direction.CW)
            path.fillType = Path.FillType.EVEN_ODD
            clipPath(path.asComposePath()) {
                drawCircle(
                    getColor("#279144"),
                    radius = completeSize
                )
                val sizeText = measurable.measure(showText, textStyle).size
                drawText(
                    measurable,
                    showText,
                    Offset(
                        size.width.div(2).minus(sizeText.width.div(2)),
                        size.height.div(2).minus(sizeText.height.div(2)),
                    ),
                    style = textStyle
                )
            }
        }
    }
}