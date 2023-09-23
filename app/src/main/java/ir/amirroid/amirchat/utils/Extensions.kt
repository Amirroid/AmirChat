package ir.amirroid.amirchat.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.FileUtils
import android.widget.ImageView
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.media.FileModel
import ir.amirroid.amirchat.data.models.media.MediaConvertModel
import ir.amirroid.amirchat.data.models.media.MediaModel
import ir.amirroid.amirchat.data.models.media.MusicModel
import ir.amirroid.amirchat.data.models.media.MusicModelForJson
import ir.amirroid.amirchat.data.models.register.UserModel
import java.net.URLConnection
import java.text.SimpleDateFormat

fun String.checkMobile() = contains(Regex("^(\\+98|0)?9\\d{9}\$"))

fun Int.toDp(context: Context) = this * context.resources.displayMetrics.density
fun Float.toDp(context: Context) = this * context.resources.displayMetrics.density

fun Offset.toIntOffset() = IntOffset(x.toInt(), y.toInt())

fun ImageView.setTint(color: Int) = setColorFilter(
    color, PorterDuff.Mode.SRC_IN
)

fun MediaModel.getType() = URLConnection.guessContentTypeFromName(name) ?: ""
fun String.getType() = URLConnection.guessContentTypeFromName(this) ?: "text/plain"


fun Long.formatTime(): String {
    val seconds = (this / 1000 % 60).toInt().toString()
    val minutes = (this / 1000 / 60).toInt().toString()
    val m = if (minutes.length == 1) "0$minutes" else minutes
    val s = if (seconds.length == 1) "0$seconds" else seconds
    return "${m}:$s"
}

@SuppressLint("SimpleDateFormat")
fun Long.formatTimeHourMinute() = SimpleDateFormat("HH:mm").format(this) ?: ""

@SuppressLint("SimpleDateFormat")
fun Long.formatDateTime() =
    SimpleDateFormat("yyyy/MM/dd - HH:mm").format(this) ?: System.currentTimeMillis().toString()


@SuppressLint("SimpleDateFormat")
fun Long.formatDateTimeForFile() =
    SimpleDateFormat("yyyy-MM-dd-HH-ss").format(this) ?: this.toString()

@SuppressLint("SimpleDateFormat")
fun Long.getMilliseconds(): String {
    return (this % 1000).div(100).toInt().toString()
}

fun Long.bytesToHumanReadableSize(): String {
    val kiloByteAsByte = 1.0 * 1024.0
    val megaByteAsByte = 1.0 * 1024.0 * 1024.0
    val gigaByteAsByte = 1.0 * 1024.0 * 1024.0 * 1024.0
    val teraByteAsByte = 1.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0
    val petaByteAsByte = 1.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0

    return when {
        this < kiloByteAsByte -> "${this.toDouble()} B"
        this >= kiloByteAsByte && this < megaByteAsByte -> "${
            String.format(
                "%.2f",
                (this / kiloByteAsByte)
            )
        } KB"

        this >= megaByteAsByte && this < gigaByteAsByte -> "${
            String.format(
                "%.2f",
                (this / megaByteAsByte)
            )
        } MB"

        this >= gigaByteAsByte && this < teraByteAsByte -> "${
            String.format(
                "%.2f",
                (this / gigaByteAsByte)
            )
        } GB"

        this >= teraByteAsByte && this < petaByteAsByte -> "${
            String.format(
                "%.2f",
                (this / teraByteAsByte)
            )
        } TB"

        else -> "Bigger than 1024 TB"
    }
}

fun FileModel.getImage(): Any {
    val mimeTypeDocuments = listOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

    return when {
        mimeType.startsWith("image") -> data
        mimeType.startsWith("video") -> R.drawable.ic_video
        mimeType.startsWith("audio") -> R.drawable.ic_music
        mimeTypeDocuments.contains(mimeType) -> R.drawable.ic_document
        else -> R.drawable.ic_file
    }
}

fun Int.divIfNotZero(other: Int): Int {
    if (this == 0 || other == 0) {
        return 0
    }
    return this / other
}

fun Int.divIfNotZero(other: Float): Float {
    if (this == 0 || other == 0f) {
        return 0f
    }
    return this / other
}

fun Float.divIfNotZero(other: Float): Float {
    if (this == 0f || other == 0f) {
        return 0f
    }
    return this / other
}

fun Float.divIfNotZero(other: Int): Float {
    if (this == 0f || other == 0) {
        return 0f
    }
    return this / other
}

fun Size.toDpSize(density: Density) = DpSize(
    with(density) { width.toDp() },
    with(density) { height.toDp() },
)


fun UserModel.getName() = "$firstName $lastName"

val ChatRoom.id: String
    get() = "${this.from.token}-${this.to.token}"


fun MusicModel.toJsonMusic() = MusicModelForJson(
    name,
    artistName,
    data,
    duration,
    id,
    uri.toString()
)

fun MediaModel.toMediaJson() = MediaConvertModel(
    name,
    data,
    duration,
    id,
    uri.toString(),
    dateAdded
)

fun <T> MutableList<T>.addAllIf(
    list: List<T>,
    condition: List<T>.(obj1: T) -> Boolean
): List<T> {
    list.forEach { model ->
        if (condition.invoke(this, model)) {
            add(model)
        }
    }
    return this.toList()
}

fun MessageModel.getText(context: Context) = when {
    message.isEmpty() -> getTextForFileType(files.first().type, context)
    else -> message
}

fun HapticFeedback.startLongPress() {
    performHapticFeedback(HapticFeedbackType.LongPress)
}