package ir.amirroid.amirchat.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.OnReceiveContentListener
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.widget.addTextChangedListener
import ir.amirroid.amirchat.utils.toDp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StickerTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    placeHolder: String,
    showKeyboard: Boolean,
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    maxHeight: Float = 200.toDp(context),
    onFocusChanged: (Boolean) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val cursorColor = MaterialTheme.colorScheme.primary
    val cursorDrawable = remember {
        generateDrawableCursor(cursorColor.toArgb(), context)
    }
    val editText = remember {
        val editTextObj = object : EditText(context) {
            override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
                val connection = super.onCreateInputConnection(outAttrs)
                EditorInfoCompat.setContentMimeTypes(
                    outAttrs,
                    arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg",
                        "image/gif",
                    )
                )
                val callback = stickerCallback { }
                return InputConnectionCompat.createWrapper(connection, outAttrs, callback)
            }
        }

        editTextObj.apply {
            background = null
            hint = placeHolder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textCursorDrawable = cursorDrawable
            }
            setTextColor(textColor.toArgb())
            setHintTextColor(textColor.toArgb())
            highlightColor = cursorColor.copy(0.6f).toArgb()
            addTextChangedListener { value ->
                onValueChanged.invoke(value.toString())
            }
            setOnFocusChangeListener { _, b -> onFocusChanged.invoke(b) }
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            this.maxHeight = maxHeight.toInt()
        }
        editTextObj
    }
    val keyboard = context.getSystemService(InputMethodManager::class.java)
    LaunchedEffect(key1 = showKeyboard) {
        if (showKeyboard.not()) {
            editText.clearFocus()
            keyboard.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }
    DisposableEffect(key1 = Unit) {
        editText.requestFocus()
        keyboard.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        onDispose {}
    }
    LaunchedEffect(key1 = value) {
        if (editText.text.toString() != value) {
            editText.setText(value)
        }
    }
    AndroidView(factory = {
        editText
    }, modifier = modifier)
}

fun generateDrawableCursor(color: Int, context: Context): Drawable {
    val bitmap = Bitmap.createBitmap(
        2.toDp(context).toInt(),
        8.toDp(context).toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawRoundRect(
        0f,
        0f,
        bitmap.width.toFloat(),
        bitmap.height.toFloat(),
        0.5f.toDp(context),
        0.5f.toDp(context),
        Paint().apply { this.color = color })
    return bitmap.toDrawable(context.resources)
}

fun stickerCallback(onCallBack: (Uri) -> Unit) =
    InputConnectionCompat.OnCommitContentListener { inputContentInfo, _, _ ->
        try {
            inputContentInfo.releasePermission()
        } catch (_: Exception) {
        }
        val uri = inputContentInfo.contentUri
        onCallBack.invoke(uri)
        true
    }