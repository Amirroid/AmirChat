package ir.amirroid.amirchat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OtpView(
    code: String,
    length: Int,
    correct: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        for (i in 0 until length) {
            val text = code.getOrNull(i) ?: ""
            val enabled = code.length == i
            Otp(text = text.toString(), enabled = enabled, correct)
        }
    }
}

@Composable
fun Otp(
    text: String,
    enabled: Boolean,
    correct: Boolean
) {
    val color by animateColorAsState(
        targetValue = when {
            correct -> Color.Green
            enabled -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "",
        animationSpec = tween(300)
    )
    var previewText by remember {
        mutableStateOf(text)
    }
    var visible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = text) {
        visible = text.isNotEmpty()
        if (text.isNotEmpty()) {
            previewText = text
        }
    }
    Box(
        modifier = Modifier
            .size(50.dp)
            .border(2.dp, color, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically { 30 } + fadeIn(),
            exit = slideOutVertically { 30 } + fadeOut(),
            modifier = Modifier.wrapContentSize()
        ) {
            Text(text = previewText)
        }
    }
}