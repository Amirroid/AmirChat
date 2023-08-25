package ir.amirroid.amirchat.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.register.CurrentUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMediaPopUp(
    show: Boolean,
    size: Size,
    offset: Offset,
    file: FileMessage?,
    message: String,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    var showPopUp by remember {
        mutableStateOf(false)
    }
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    LaunchedEffect(key1 = show) {
        if (show) {
            try {
                bitmap = BitmapFactory.decodeFile(file?.fromPath)
                showPopUp = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ImageRequest.Builder(context).data(file?.path)
                .allowHardware(true)
                .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
            showPopUp = true
        }
    }
    MediaPopUp(show = show, size = size, offset = offset, mediaContent = {
        Zoomable {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = if (it) ContentScale.Fit else ContentScale.Crop,
                    filterQuality = FilterQuality.High
                )
            }
        }
    }, overlyContent = {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            SmallTopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = onDismissRequest) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "back")
                }
            }, colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent))
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
            )
        }
    }, onDismissRequest = onDismissRequest)
}