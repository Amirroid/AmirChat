package ir.amirroid.amirchat.ui.components

import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.media.MediaModel
import ir.amirroid.amirchat.utils.getBasicAlphaColorsOfTextField
import ir.amirroid.amirchat.utils.getStatusBarHeight
import ir.amirroid.amirchat.utils.getType
import ir.amirroid.amirchat.utils.toDpSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.absoluteValue


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaPopUpWithAnimation(
    show: Boolean,
    size: Size,
    offset: Offset,
    media: MediaModel?,
    selected: Boolean,
    count: Int,
    onSelect: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onSend: () -> Unit
) {
    var isPlayingVideo by remember {
        mutableStateOf(false)
    }
    var currentPosition by remember {
        mutableLongStateOf(0L)
    }
    var duration by remember {
        mutableLongStateOf(0L)
    }
    var changeToPosition by remember {
        mutableLongStateOf(0L)
    }
    if (media != null)
        MediaPopUp(show = show, size = size, offset = offset, mediaContent = { showImage ->
            Zoomable {
                MediaViewer(
                    media, showImage, changeToPosition
                ) { d, p, play ->
                    duration = d
                    currentPosition = p
                    isPlayingVideo = play
                    Log.d("desjifcps", "MediaPopUpWithAnimation: $currentPosition")
                }
            }
        }, onDismissRequest = onDismissRequest, overlyContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(title = {
                    Text(text = media.name, maxLines = 1)
                }, navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "back")
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(0.6f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ), actions = {
                    Spacer(modifier = Modifier.width(6.dp))
                    AnimatedVisibility(visible = count != 0) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = count.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    SelectionButton(
                        checked = selected,
                        modifier = Modifier
                            .size(32.dp)
                            .toggleable(selected, onValueChange = onSelect)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                })
                if (isPlayingVideo.not() && media.getType().startsWith("video")) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(Color.Black.copy(0.5f))
                            .size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "plaay",
                            tint = Color.White
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        media.getType().startsWith("video"),
                        enter = expandVertically(expandFrom = Alignment.Top),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        TrimMediaProgressBar(
                            path = media.data,
                            duration = media.duration,
                            position = currentPosition,
                        ) {
                            changeToPosition = it
                        }
                    }
                    FloatingActionButton(
                        onClick = onSend,
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
                        modifier = Modifier
                            .padding(bottom = 20.dp, end = 12.dp)
                            .align(Alignment.End)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_send_24),
                            contentDescription = "send",
                        )
                    }
                }
            }
        })
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun MediaViewer(
    media: MediaModel,
    show: Boolean,
    changeToPosition: Long,
    onVideoEvent: (
        duration: Long, currentPosition: Long, play: Boolean
    ) -> Unit,
) {
    var image by remember {
        mutableStateOf<Any?>(null)
    }
    val type = media.getType()
    LaunchedEffect(key1 = Unit) {
        if (image == null) {
            if (type.startsWith("video", true)) {
                withContext(Dispatchers.IO) {
                    val bitmap = ThumbnailUtils.createVideoThumbnail(
                        media.data, MediaStore.Images.Thumbnails.MINI_KIND
                    )
                    image = bitmap
                }
            } else {
                image = media.uri
            }
        }
    }
    if (type.startsWith("video")) {
        VideoViewBasic(
            videoUri = media.uri,
            modifier = Modifier.fillMaxSize(),
            onVideoEvent = onVideoEvent,
            changePosition = changeToPosition,
            play = false,
            playPause = true

        )
    } else {
        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = if (show) ContentScale.Fit else ContentScale.Crop,
            filterQuality = FilterQuality.High
        )
    }
}

@Composable
fun MediaPopUp(
    show: Boolean,
    size: Size,
    offset: Offset,
    mediaContent: @Composable (Boolean) -> Unit,
    overlyContent: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val matrix = context.resources.displayMetrics
    val width = matrix.widthPixels.toFloat()
    val height = matrix.heightPixels.toFloat()
    val offsetAnimateX = remember {
        Animatable(0f)
    }
    val offsetAnimateY = remember {
        Animatable(0f)
    }
    val widthAnimation = remember {
        Animatable(0f)
    }
    val heightAnimation = remember {
        Animatable(0f)
    }
    var showPopUp by remember {
        mutableStateOf(false)
    }
    var crop by remember {
        mutableStateOf(false)
    }
    val statusBarHeight = getStatusBarHeight(context)
    val backgroundColor = remember {
        androidx.compose.animation.Animatable(Color.Black)
    }
    LaunchedEffect(key1 = show) {
        launch {
            if (show.not()) {
                delay(100)
                crop = show
                delay(200)
            }
            showPopUp = show
        }
        if (show) {
            delay(200)
            launch {
                offsetAnimateX.snapTo(offset.x)
                offsetAnimateY.snapTo(offset.y - statusBarHeight)
                widthAnimation.snapTo(size.width)
                heightAnimation.snapTo(size.height)
                launch { offsetAnimateX.animateTo(0f) }
                launch { offsetAnimateY.animateTo(0f) }
                launch { widthAnimation.animateTo(width) }
                launch { heightAnimation.animateTo(height) }
                backgroundColor.snapTo(Color.Black)
            }
        } else {
            launch { offsetAnimateX.animateTo(offset.x) }
            launch { offsetAnimateY.animateTo(offset.y - statusBarHeight) }
            launch { widthAnimation.animateTo(size.width) }
            launch { heightAnimation.animateTo(size.height) }
        }
    }
    val ins = remember {
        MutableInteractionSource()
    }
    if (showPopUp) Popup(
        onDismissRequest = onDismissRequest
    ) {
        Box {
            AnimatedVisibility(
                visible = show, enter = fadeIn(tween(300)), exit = fadeOut(tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor.value)
                )
            }
        }
        Box(modifier = Modifier
            .clickable(
                ins,
                null,
            ) {
                onClick?.invoke()
            }
            .draggable(rememberDraggableState {
                scope.launch {
                    offsetAnimateY.snapTo(offsetAnimateY.value.plus(it))
                    launch {
                        if (abs(offsetAnimateY.value) > height * 0.12f) {
                            if (backgroundColor.value != Color.Black.copy(0.6f)) {
                                backgroundColor.animateTo(Color.Black.copy(0.6f))
                            }
                        } else {
                            if (backgroundColor.value != Color.Black) {
                                backgroundColor.animateTo(Color.Black)
                            }
                        }
                    }
                }
            }, Orientation.Vertical, onDragStopped = {
                if (abs(offsetAnimateY.value) > height * 0.12f) {
                    onDismissRequest.invoke()
                } else {
                    scope.launch {
                        offsetAnimateY.animateTo(0f)
                    }
                }
            })
            .offset {
                IntOffset(
                    offsetAnimateX.value.toInt(), offsetAnimateY.value.toInt()
                )
            }
            .size(Size(widthAnimation.value, heightAnimation.value).toDpSize(density))
            .clipToBounds(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                mediaContent.invoke(crop)
            }
        }
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            overlyContent.invoke()
        }
    }
}