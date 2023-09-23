package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.utils.getType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@SuppressLint("UnsafeOptInUsageError")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ChatMediaPopUp(
    show: Boolean,
    size: Size,
    offset: Offset,
    file: FileMessage?,
    message: MessageModel?,
    onDismissRequest: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = message?.files?.indexOf(file) ?: 1) {
        message?.files?.size ?: 1
    }
    LaunchedEffect(key1 = show) {
        pagerState.scrollToPage(message?.files?.indexOf(file) ?: 1)
    }
    val lazyState = rememberLazyListState()
    val snapState = rememberSnapFlingBehavior(lazyListState = lazyState)
    val scope = rememberCoroutineScope()
    var play by remember {
        mutableStateOf(false)
    }
    var progress by remember {
        mutableLongStateOf(0L)
    }
    var duration by remember {
        mutableLongStateOf(0L)
    }
    var playButtonShow by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = playButtonShow) {
        if (playButtonShow) {
            delay(3000)
            playButtonShow = false
        }
    }
    MediaPopUp(show = show, size = size, offset = offset, mediaContent = {
        MediaContent(message, pagerState, play, it, progress) { cProgress, cDuration, isPlay ->
            progress = cProgress
            duration = cDuration
            play = isPlay
        }
        Log.d("dsfdsf", "ChatMediaPopUp: $it")
    }, onClick = {
                 playButtonShow= playButtonShow.not()
    }, overlyContent = {
        val context = LocalContext.current
        val imageLoader = ImageLoader(context)
        val videoLoader = ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
        AnimatedVisibility(
            visible = playButtonShow, enter = fadeIn(), exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
            ) {
                SmallTopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
                )
                if (message?.files?.getOrNull(pagerState.currentPage.minus(1))?.path?.getType()
                        ?.startsWith("video") == true
                ) {
                    PlayButton(
                        play = play,
                        onPlayRequest = { play = it },
                        modifier = Modifier.size(64.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Black.copy(0.6f))
                        .padding(vertical = 12.dp)
                ) {
                    if (message != null) {
                        val files = message.files
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            state = lazyState,
                            flingBehavior = snapState,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            items(files.size, key = { files[it].fromPath }) {
                                val currentFile = files[it]
                                val isVideo = currentFile.fromPath.startsWith("video")
                                val isMyFrom = message.from == CurrentUser.token
                                ImagePreview(
                                    isVideo,
                                    isMyFrom,
                                    if (isVideo) videoLoader else imageLoader,
                                    context,
                                    currentFile
                                ) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(it)
                                    }
                                }
                                if (it != files.size) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                        Text(
                            text = message.message,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                        )
                        AnimatedVisibility(
                            visible = files.getOrNull(pagerState.currentPage.minus(1))?.path?.getType()
                                ?.startsWith("video") == true
                        ) {
                            Slider(value = progress.toFloat(), onValueChange = {
                                progress = it.toLong()
                            }, valueRange = 0f..duration.toFloat())
                        }
                    }
                }
            }
        }
    }, onDismissRequest = onDismissRequest)
}

@Composable
fun ImagePreview(
    isVideo: Boolean,
    isMyFrom: Boolean,
    imageLoader: ImageLoader,
    context: Context,
    file: FileMessage,
    onClick: () -> Unit
) {
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    LaunchedEffect(key1 = file) {
        if (bitmap == null) {
            if (isVideo) {
                if (isMyFrom) {
                    val request =
                        ImageRequest.Builder(context).data(file.fromPath)
                            .target { b ->
                                bitmap = (b as BitmapDrawable).bitmap
                            }.build()
                    imageLoader.enqueue(request)
                }
                val request = ImageRequest.Builder(context).data(file.path)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                imageLoader.enqueue(request)
            } else {
                if (isMyFrom) {
                    bitmap = BitmapFactory.decodeFile(file.fromPath)
                }
                val request = ImageRequest.Builder(context).data(file.path)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                imageLoader.enqueue(request)
            }
        }

    }
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(
                RoundedCornerShape(4.dp)
            )
    ) {
        AsyncImage(
            model = bitmap, contentDescription = null, modifier = Modifier
                .clickable(onClick = onClick), contentScale = ContentScale.Crop
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
@UnstableApi
fun MediaContent(
    message: MessageModel?,
    pagerState: PagerState,
    play: Boolean,
    show: Boolean,
    progress: Long,
    onEvent: (Long, Long, Boolean) -> Unit
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader(context)
    if (message != null) {
        val isMyFrom = message.from == CurrentUser.token
        val files = message.files
        HorizontalPager(state = pagerState) {
            val file = files[it]
            if (file.path.getType().startsWith("video")) {
                VideoView(
                    context,
                    isMyFrom,
                    file,
                    onEvent,
                    play,
                    progress
                )
            } else {
                onEvent.invoke(0L, 1L, false)
                ImageView(
                    file,
                    show,
                    isMyFrom,
                    context,
                    imageLoader
                )
            }
        }
    }
}

@Composable
fun ImageView(
    file: FileMessage,
    show: Boolean,
    isMyFrom: Boolean,
    context: Context,
    imageLoader: ImageLoader
) {
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    LaunchedEffect(key1 = Unit) {
        if (bitmap == null) {
            if (isMyFrom) {
                bitmap = BitmapFactory.decodeFile(file.fromPath)
            }
            val request = ImageRequest.Builder(context).data(file.path)
                .diskCachePolicy(CachePolicy.ENABLED)
                .allowHardware(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
            imageLoader.enqueue(request)
        }
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(), contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = if (show) ContentScale.Fit else ContentScale.Crop,
        )
    }
}

@Composable
@UnstableApi
fun VideoView(
    context: Context,
    isMyFrom: Boolean,
    file: FileMessage,
    onEvent: (Long, Long, Boolean) -> Unit,
    play: Boolean,
    progress: Long
) {
    LaunchedEffect(key1 = Unit) {
        onEvent.invoke(0, 1, false)
    }
    val path = if (isMyFrom && File(file.fromPath).exists()) {
        file.fromPath
    } else file.path
    val audioAttributes = remember {
        AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()
    }
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .build()
            .apply {
                setMediaSource(
                    ProgressiveMediaSource.Factory(
                        DefaultDataSource.Factory(context)
                    ).createMediaSource(MediaItem.fromUri(Uri.parse(path)))
                )
                prepare()
            }
    }
    DisposableEffect(key1 = Unit) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                onEvent.invoke(exoPlayer.currentPosition, exoPlayer.duration, exoPlayer.isPlaying)
                super.onPlaybackStateChanged(playbackState)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }
    LaunchedEffect(key1 = play) {
        if (play) {
            repeat(Int.MAX_VALUE) {
                delay(1000)
                onEvent.invoke(exoPlayer.currentPosition, exoPlayer.duration, exoPlayer.isPlaying)
            }
        }
    }
    val playerView = remember {
        PlayerView(context).apply {
            useController = false
            hideController()
            player = exoPlayer
        }
    }
    LaunchedEffect(key1 = play, progress) {
        exoPlayer.seekTo(progress)
        if (play) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }
    AndroidView(factory = {
        playerView
    }, modifier = Modifier.fillMaxWidth())
}
