package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.events.MessageEvents
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.media.ContactModel
import ir.amirroid.amirchat.data.models.media.Location
import ir.amirroid.amirchat.data.models.media.MusicModelForJson
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.formatTime
import ir.amirroid.amirchat.utils.formatTimeHourMinute
import ir.amirroid.amirchat.utils.getColorOfMessage
import ir.amirroid.amirchat.utils.getEmoji
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.utils.getShapeOfMessage
import ir.amirroid.amirchat.utils.getTextColorOfMessage
import ir.amirroid.amirchat.utils.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MessageView(
    maxWidth: Dp,
    message: MessageModel,
    isMyUser: Boolean,
    replyEnabled: Boolean = true,
    playingMusic: Uri,
    onMessageEvent: ((MessageEvents) -> Unit)? = null,
    currentPosition: Long? = null,
    onContentClick: ((Offset, Size, Pair<MessageModel, FileMessage>) -> Unit)? = null,
    replyMessage: MessageModel?,
    selected: Boolean,
    to: UserModel,
    selectionMode: Boolean
) {
    val context = LocalContext.current
    var size by remember {
        mutableFloatStateOf(0f)
    }
    var lastOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    val selectionSize = remember {
        Animatable(0f)
    }
    var position by remember {
        mutableStateOf(Offset.Zero)
    }
    LaunchedEffect(key1 = selected) {
        if (selected) {
            selectionSize.animateTo(size * 2, tween(500))
        } else {
            selectionSize.animateTo(0f, tween(500))
        }
    }
    val selectedColor = MaterialTheme.colorScheme.primaryContainer.copy(0.3f)
    Box(modifier = Modifier
        .fillMaxWidth()
        .onGloballyPositioned {
            size = maxOf(it.size.width, it.size.height).toFloat()
            position = it.positionInWindow()
        }
        .pointerInput(selected, selectionMode, position, message) {
            detectTapGestures(onLongPress = {
                lastOffset = it
                onMessageEvent?.invoke(MessageEvents.LongClick(message))
            }, onTap = {
                if (selectionMode) {
                    lastOffset = it
                    onMessageEvent?.invoke(MessageEvents.LongClick(message))
                } else {
                    onMessageEvent?.invoke(MessageEvents.Click(message, position))
                }
            }, onDoubleTap = {
                onMessageEvent?.invoke(MessageEvents.SetEmoji(message, getEmoji(0X1F44D)))
            })
        }
        .drawWithContent {
            clipRect {
                drawCircle(selectedColor, radius = selectionSize.value, lastOffset)
            }
            drawContent()
        }) {
        SwipeBox(
            paddingEnd = if (isMyUser) 0.dp else 12.dp,
            enabled = replyEnabled,
            onReplyRequest = {
                onMessageEvent?.invoke(MessageEvents.Reply(message.id))
            }) {
            if (message.files.firstOrNull()?.type == Constants.STICKER) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context).data(message.files.first()).build(),
                        imageLoader = ImageLoader.Builder(context)
                            .allowHardware(true)
                            .components {
                                if (SDK_INT >= 28) {
                                    add(ImageDecoderDecoder.Factory())
                                } else {
                                    add(GifDecoder.Factory())
                                }
                            }.build()
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            } else {
                MessageView(
                    message = message,
                    maxWidth = maxWidth,
                    isMyUser,
                    playingMusic,
                    currentPosition,
                    onContentClick,
                    onMessageEvent,
                    replyMessage,
                    to
                )
            }
        }
    }
}


@Composable
fun RowScope.MessageView(
    message: MessageModel,
    maxWidth: Dp,
    isMyUser: Boolean,
    playingMusic: Uri,
    currentPosition: Long? = null,
    onContentClick: ((Offset, Size, Pair<MessageModel, FileMessage>) -> Unit)? = null,
    onMessageEvent: ((MessageEvents) -> Unit)?,
    replyMessage: MessageModel?,
    to: UserModel
) {
    Box(
        modifier = Modifier
            .weight(1f),
        contentAlignment = if (isMyUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .offset(x = if (isMyUser) 18.dp else 0.dp)
                .widthIn(
                    max = maxWidth
                )
                .clip(
                    getShapeOfMessage(isMyUser)
                )
                .background(getColorOfMessage(isMyUser = isMyUser))
        ) {
            if (replyMessage != null) {
                val name = if (replyMessage.from == CurrentUser.token) {
                    CurrentUser.user?.getName() ?: ""
                } else to.getName()
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }) {
                        val density = LocalDensity.current
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var sizeBox by remember {
                                mutableIntStateOf(0)
                            }
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .height(with(density) { sizeBox.toDp() } - 12.dp)
                                    .width(4.dp)
                            )
                            Column(modifier = Modifier
                                .onSizeChanged {
                                    sizeBox = it.height
                                }
                                .padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = replyMessage.message,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
            if (message.files.isNotEmpty()) {
                FilesContent(
                    message,
                    currentPosition,
                    playingMusic,
                    onMessageEvent
                ) { offset, size, file ->
                    onContentClick?.invoke(offset, size, Pair(message, file))
                }
            }
            Column(
                modifier = Modifier
                    .then(
                        if (message.files.isNotEmpty()) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (message.message.isNotEmpty()) {
                    SelectionContainer {
                        Text(
                            text = message.message,
                            modifier = Modifier,
                            color = getTextColorOfMessage(isMyUser = isMyUser)
                        )
                    }
                }
                if (message.fromEmoji != null || message.toEmoji != null) {
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        val fromUser = message.chatRoom.split("-").first() == CurrentUser.token
                        val toUser = message.chatRoom.split("-").last() == CurrentUser.token
                        if (message.fromEmoji != null) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (fromUser) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primaryContainer
                                    )
                                    .wrapContentSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            if (fromUser) {
                                                onMessageEvent?.invoke(
                                                    MessageEvents.SetEmoji(
                                                        message,
                                                        null
                                                    )
                                                )
                                            }else{
                                                onMessageEvent?.invoke(
                                                    MessageEvents.SetEmoji(
                                                        message,
                                                        message.fromEmoji
                                                    )
                                                )
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = message.fromEmoji)
                                }
                            }
                        }
                        if (message.toEmoji != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (toUser) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primaryContainer
                                    )
                                    .wrapContentSize(),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            if (toUser) {
                                                onMessageEvent?.invoke(
                                                    MessageEvents.SetEmoji(
                                                        message,
                                                        null
                                                    )
                                                )
                                            }else{
                                                onMessageEvent?.invoke(
                                                    MessageEvents.SetEmoji(
                                                        message,
                                                        message.toEmoji
                                                    )
                                                )
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = message.toEmoji)
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = if (message.files.isNotEmpty()) 8.dp else 4.dp)
                        .alpha(0.7f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.date.formatTimeHourMinute(),
                        style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurface)
                    )
                    val icon = when (message.status) {
                        Constants.SENDING -> R.drawable.outline_watch_later_24
                        Constants.SEND -> R.drawable.baseline_done_24
                        Constants.SEEN -> R.drawable.baseline_done_all_24
                        else -> R.drawable.outline_watch_later_24
                    }
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilesContent(
    message: MessageModel,
    currentPosition: Long? = null,
    playingMusic: Uri,
    onMessageEvent: ((MessageEvents) -> Unit)? = null,
    onContentClick: ((Offset, Size, FileMessage) -> Unit)?
) {
    when (message.files.first().type) {
        Constants.GALLERY -> {
            GalleryView(message, onContentClick)
        }

        Constants.MUSIC -> {
            MusicsView(message, currentPosition, playingMusic, onContentClick, onMessageEvent)
        }

        Constants.CONTACT -> {
            ContactView(message)
        }

        Constants.LOCATION -> {
            LocationView(Gson().fromJson(message.files.first().data, Location::class.java))
        }
    }
}

@Composable
fun LocationView(location: Location) {
    val camera = rememberCameraPositionState()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        camera.move(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.lat, location.lng),
                location.zoom
            )
        )
    }
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(150.dp)
            .clip(MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapStyleOptions = if (isSystemInDarkTheme()) MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.dark_map
                ) else null,
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                zoomGesturesEnabled = false,
            ),
            cameraPositionState = camera
        ) {

        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_location_on_24),
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun ContactView(message: MessageModel) {
    val contact = Gson().fromJson(message.files.first().data, ContactModel::class.java)
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            NameView(name = contact.name, brush = Constants.randomBrush.first(), circleShape = true)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                contact.numbers.forEach {
                    Text(text = it, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        OutlinedButton(
            onClick = {}, modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.view_contact),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun MusicsView(
    message: MessageModel,
    currentPosition: Long? = null,
    playingMusic: Uri, onContentClick: ((Offset, Size, FileMessage) -> Unit)?,
    onMessageEvent: ((MessageEvents) -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        message.files.forEach { file ->
            MusicContent(file, playingMusic, onContentClick, currentPosition!!, onMessageEvent)
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun MusicContent(
    file: FileMessage,
    playingMusic: Uri,
    onContentClick: ((Offset, Size, FileMessage) -> Unit)?,
    currentPosition: Long,
    onMessageEvent: ((MessageEvents) -> Unit)?
) {
    var progress by remember(currentPosition) {
        mutableFloatStateOf(currentPosition.toFloat())
    }
    val data = Gson().fromJson(file.data, MusicModelForJson::class.java)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayButton(
            play = playingMusic.toString() == file.path,
            color = MaterialTheme.colorScheme.primary
        ) {
            onContentClick?.invoke(Offset.Zero, Size.Zero, file)
        }
        AnimatedContent(
            targetState = playingMusic.toString() == file.path,
            label = "",
            modifier = Modifier.padding(start = 12.dp)
        ) {
            if (it) {
                Column {
                    Slider(
                        value = if (playingMusic.toString() == file.path) progress else 0f,
                        onValueChange = { p ->
                            progress = p
                            onMessageEvent?.invoke(
                                MessageEvents.SeekExo(
                                    p.toLong()
                                )
                            )
                        },
                        valueRange = 0f..data.duration.toFloat()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentPosition.formatTime(),
                            modifier = Modifier
                                .alpha(0.7f)
                                .wrapContentSize(),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        Text(
                            text = data.duration.minus(currentPosition).formatTime(),
                            modifier = Modifier
                                .alpha(0.7f)
                                .wrapContentSize(),
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }
                }
            } else {
                Column {
                    Text(text = data.name, style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = data.artistName,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .weight(1f),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = data.duration.formatTime(),
                            modifier = Modifier
                                .alpha(0.7f)
                                .wrapContentSize(),
                            style = TextStyle(fontSize = 12.sp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryView(message: MessageModel, onContentClick: ((Offset, Size, FileMessage) -> Unit)?) {
    val files = message.files
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        if (files.size == 1) {
            var bitmap by remember {
                mutableStateOf<Bitmap?>(null)
            }
            var offset by remember {
                mutableStateOf(Offset.Zero)
            }
            var size by remember {
                mutableStateOf(Size.Zero)
            }
            LaunchedEffect(key1 = Unit) {
                if (message.from == CurrentUser.token) {
                    bitmap = BitmapFactory.decodeFile(files.first().fromPath)
                    ImageRequest.Builder(context).data(files.first().path)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                } else {
                    ImageRequest.Builder(context).data(files.first().path)
                        .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                }
            }
            AsyncImage(
                model = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable(enabled = onContentClick != null) {
                        onContentClick?.invoke(offset, size, files.first())
                    }
                    .onGloballyPositioned {
                        offset = it.positionInWindow()
                        size = it.size.toSize()
                    },
                contentScale = ContentScale.Crop
            )
        } else {
            Column {
                for (i in (0..files.size.plus(1).div(2).minus(1))) {
                    val index = i * 2
                    Row(modifier = Modifier.fillMaxWidth()) {
                        var offset by remember {
                            mutableStateOf(Offset.Zero)
                        }
                        var size by remember {
                            mutableStateOf(Size.Zero)
                        }
                        var bitmap by remember {
                            mutableStateOf<Bitmap?>(null)
                        }
                        var bitmap2 by remember {
                            mutableStateOf<Bitmap?>(null)
                        }
                        LaunchedEffect(key1 = Unit) {
                            if (message.from == CurrentUser.token) {
                                bitmap = BitmapFactory.decodeFile(files[index].fromPath)
                                ImageRequest.Builder(context).data(files[index].path)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }
                                    .build()
                                if (files.size.minus(1) >= index.plus(1)) {
                                    bitmap2 =
                                        BitmapFactory.decodeFile(files[index.plus(1)].fromPath)
                                    ImageRequest.Builder(context)
                                        .data(files[index.plus(1)].path)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .target { b ->
                                            bitmap2 = (b as BitmapDrawable).bitmap
                                        }
                                        .build()
                                }
                            } else {
                                ImageRequest.Builder(context).data(files[index].path)
                                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }
                                    .build()
                                if (files.size.minus(1) >= index.plus(1)) {
                                    ImageRequest.Builder(context)
                                        .data(files[index.plus(1)].path)
                                        .target { b ->
                                            bitmap2 = (b as BitmapDrawable).bitmap
                                        }
                                        .build()
                                }
                            }
                        }
                        AsyncImage(
                            model = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(max = 200.dp)
                                .clickable(enabled = onContentClick != null) {
                                    onContentClick?.invoke(offset, size, files[index])
                                }
                                .onGloballyPositioned {
                                    offset = it.positionInWindow()
                                    size = it.size.toSize()
                                },
                            contentScale = ContentScale.Crop
                        )
                        if (files.size.minus(1) >= index.plus(1)) {
                            var offset2 by remember {
                                mutableStateOf(Offset.Zero)
                            }
                            var size2 by remember {
                                mutableStateOf(Size.Zero)
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                            AsyncImage(
                                model = bitmap2,
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(max = 200.dp)
                                    .clickable(enabled = onContentClick != null) {
                                        onContentClick?.invoke(offset2, size2, files[index.plus(1)])
                                    }
                                    .onGloballyPositioned {
                                        offset2 = it.positionInWindow()
                                        size2 = it.size.toSize()
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    if (files.size >= index.plus(2)) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeBox(
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    paddingEnd: Dp,
    enabled: Boolean,
    onReplyRequest: () -> Unit,
    content: @Composable RowScope.() -> Unit = {}
) {
    val offsetX = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val alphaReply by animateFloatAsState(
        targetValue = when {
            offsetX.value < -190 -> 1f
            offsetX.value > -10 -> 0f
            else -> .8f
        },
        label = ""
    )
    val hapticFeedback = LocalHapticFeedback.current
    var hapticFeedbackDone by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = offsetX.value) {
        if (offsetX.value < -190f && hapticFeedbackDone.not()) {
            hapticFeedbackDone = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            hapticFeedbackDone = false
        }
    }
    val colorReply = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    val colorReplyBorder = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val dragState = rememberDraggableState {
        scope.launch {
            offsetX.snapTo((offsetX.value + it.toInt()).coerceIn(-210f, 0f))
        }
    }
    Row(
        modifier = Modifier
            .offset {
                IntOffset(offsetX.value.toInt(), 0)
            }
            .fillMaxWidth()
            .draggable(
                dragState,
                Orientation.Horizontal,
                onDragStopped = {
                    scope.launch { offsetX.animateTo(0f, animationSpec = spring()) }
                    if (offsetX.value < -190f && hapticFeedbackDone.not()){
                        onReplyRequest.invoke()
                    }
                },
                enabled = enabled
            )
            .padding(start = 12.dp, end = paddingEnd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content.invoke(this)
        Box(modifier = Modifier
            .size(30.dp)
            .drawWithContent {
                drawCircle(
                    colorReply.copy(alphaReply),
                )
                drawArc(
                    colorReplyBorder,
                    -90f,
                    offsetX.value.div(-200) * 360,
                    false,
                    style = Stroke(
                        2.toDp(context),
                        cap = StrokeCap.Round
                    ),
                    size = Size(
                        width = size.width - 3.toDp(context),
                        height = size.height - 3.toDp(context),
                    ),
                    topLeft = Offset(
                        x = 1.5f.toDp(context),
                        y = 1.5f.toDp(context),
                    )
                )
                drawContent()
            }
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = offsetX.value < -10,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                enter = scaleIn(animationSpec = spring()),
                exit = scaleOut(animationSpec = spring())
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_reply_24),
                    contentDescription = "reply",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}