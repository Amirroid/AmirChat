package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun TextChangeAnimation(
    text: String,
    style: TextStyle = TextStyle.Default
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.toCharArray().forEach {
            AnimatedContent(targetState = it, transitionSpec = {
                slideInVertically { 100 } + fadeIn() with slideOutVertically { -100 } + fadeOut()
            }, label = "") { text ->
                Text(text = text.toString(), style = style)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun TextChangeAnimationCounter(
    text: String,
    style: TextStyle = TextStyle.Default
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.toCharArray().forEach { char ->
            AnimatedContent(targetState = char, transitionSpec = {
                if (
                    (targetState.toString().toIntOrNull() ?: 0) >
                    (initialState.toString().toIntOrNull() ?: 0)
                ) {
                    slideInVertically { 100 } + fadeIn() with slideOutVertically { -100 } + fadeOut()
                } else {
                    slideInVertically { -100 } + fadeIn() with slideOutVertically { 100 } + fadeOut()
                }
            }, label = "") { text ->
                Text(text = text.toString(), style = style)
            }
        }
    }
}