package ir.amirroid.amirchat.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.amirroid.amirchat.R
import kotlin.streams.toList

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TextAnimation(text: String) {
    var indexAnimated by remember {
        mutableIntStateOf(0)
    }
    var previewsText by remember {
        mutableStateOf("")
    }
    var deletedChar by remember {
        mutableStateOf(Pair("0", 0))
    }
    LaunchedEffect(key1 = text) {
        Log.d("regfreer", "TextAnimation: ${text.length}  ${previewsText.length}")
        if (text.length < previewsText.length) {
            deletedChar = previewsText.last().toString() to previewsText.length
            Log.d("regfreer", "TextAnimation: $deletedChar")
        }
        previewsText = text
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.toCharArray().forEachIndexed { index, char ->
            AnimatedVisibility(
                visible = true,
                initiallyVisible = index < indexAnimated,
                enter = slideInVertically { 40 } + fadeIn(),
                exit = slideOutVertically { 40 } + fadeOut(),
                modifier = Modifier.wrapContentSize()
            ) {
                indexAnimated = index
                Text(text = char.toString())
            }
            Spacer(modifier = Modifier.width(1.dp))
        }
        ReusableContent(key = deletedChar) {
            AnimatedVisibility(
                visible = false,
                initiallyVisible = true,
                enter = slideInVertically { 40 } + fadeIn(),
                exit = slideOutVertically { 40 } + fadeOut(),
                modifier = Modifier.wrapContentSize()
            ) {
                Text(text = deletedChar.first)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedTextField(text: String, error: Boolean) {
    val color by animateColorAsState(
        targetValue = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        label = ""
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(TextFieldDefaults.MinHeight)
            .border(1.5f.dp, color, MaterialTheme.shapes.medium)
            .padding(start = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Crossfade(targetState = text.isEmpty(), label = "") {
            if (it) {
                Text(
                    text = stringResource(id = R.string.phone_number),
                    modifier = Modifier.alpha(0.7f)
                )
            } else {
                TextAnimation(text = text)
            }
        }
    }
}