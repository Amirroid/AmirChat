package ir.amirroid.amirchat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.Window
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.sql.DataSource


fun getShapeOfMessage(isMyUser: Boolean) =
    RoundedCornerShape(
        topEnd = 20.dp,
        bottomEnd = if (isMyUser) 0.dp else 20.dp,
        topStart = 20.dp,
        bottomStart = if (isMyUser) 20.dp else 0.dp
    )


@Composable
fun getColorOfMessage(isMyUser: Boolean) =
    if (isMyUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh

@Composable
fun getTextColorOfMessage(isMyUser: Boolean) =
    if (isMyUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface


fun getEmoji(uniCode: Int) = String(Character.toChars(uniCode))

@SuppressLint("InternalInsetResource")
fun getStatusBarHeight(context: Context): Int {
    val resources = context.resources;
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId)
    }
    return 0
}

@SuppressLint("InternalInsetResource")
fun getNavigationBarHeight(context: Context): Int {
    val resources = context.resources;
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId)
    }
    return 0
}

fun getColor(color: String) = Color(android.graphics.Color.parseColor(color))


@Composable
fun getBasicColorsOfTextField(): TextFieldColors {
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    return colors(
        disabledIndicatorColor = Color.Unspecified,
        focusedIndicatorColor = Color.Unspecified,
        unfocusedIndicatorColor = Color.Unspecified,
        focusedContainerColor = surfaceColor,
        unfocusedContainerColor = surfaceColor,
        disabledContainerColor = surfaceColor,
    )
}

@Composable
fun getBasicAlphaColorsOfTextField(): TextFieldColors {
    return colors(
        disabledIndicatorColor = Color.Unspecified,
        focusedIndicatorColor = Color.Unspecified,
        unfocusedIndicatorColor = Color.Unspecified,
        focusedContainerColor = Color.Black.copy(0.6f),
        unfocusedContainerColor = Color.Black.copy(0.6f),
        disabledContainerColor = Color.Black.copy(0.6f),
        unfocusedTextColor = Color.White,
        focusedTextColor = Color.White,
    )
}


fun colorBrush(color: Color) = Brush.linearGradient(listOf(color, color))


fun getVideoFrames(size: Int, duration: Long, path: String, list: SnapshotStateList<ImageBitmap>) {
    try {
        val spaceSize = duration / size
        val mmr = MediaMetadataRetriever()
        for (position in 1..size) {
            mmr.setDataSource(path)
            val bitmap =
                mmr.getFrameAtTime(position * spaceSize, MediaMetadataRetriever.OPTION_CLOSEST)
            if (bitmap != null)
                list.add(bitmap.asImageBitmap())
        }
        mmr.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


val Context.preferences: DataStore<Preferences> by preferencesDataStore(
    Constants.CATCH_NAME
)