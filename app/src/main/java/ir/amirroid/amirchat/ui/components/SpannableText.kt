package ir.amirroid.amirchat.ui.components

import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import ir.amirroid.amirchat.utils.getColor


@Composable
fun SpannableText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    onContentClick: (String, isUser: Boolean) -> Unit
) {
    val linkColor = getColor("#149cdb")
    val splitText = text.split(" ")
    val generatedText = buildAnnotatedString {
        splitText.forEachIndexed { index, sText ->
            if (checkUrl(sText)){
                pushStringAnnotation(
                    sText,
                    sText
                )
                withStyle(
                    SpanStyle(
                        fontSize = 16.sp,
                        color = linkColor
                    )
                ){
                    append(sText)
                }
                pop()
            }else{
                withStyle(
                    SpanStyle(
                        fontSize = 16.sp,
                        color = color
                    )
                ){
                    append(sText)
                }
            }
            if (index.plus(1) != splitText.size){
                append(" ")
            }
        }
    }
    ClickableText(text = generatedText, onClick = { offset ->
        Log.d("dsfdsfds", "SpannableText: $offset")
        splitText.forEach { sText ->
            generatedText.getStringAnnotations(
                offset,
                offset
            ).firstOrNull()?.let {
                onContentClick.invoke(sText, sText.startsWith("@"))
                return@ClickableText true
            }
        }
        false
    })
}

fun checkUrl(text: String) = text.startsWith("@") || Patterns.WEB_URL.matcher(text).matches()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Boolean
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInteropFilter { motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP){
            layoutResult.value?.let { layoutResult ->
                return@pointerInteropFilter onClick.invoke(
                    layoutResult.getOffsetForPosition(
                        Offset(
                            motionEvent.x,
                            motionEvent.y,
                        )
                    )
                )
            }
        }else {
            return@pointerInteropFilter true
        }
        false
    }
    BasicText(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        }
    )
}