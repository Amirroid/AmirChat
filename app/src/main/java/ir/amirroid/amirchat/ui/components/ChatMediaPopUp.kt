package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import coil.Coil
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.utils.formatTime
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
    exoPlayer: ExoPlayer,
    cacheDataSource: CacheDataSource.Factory,
    onDismissRequest: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = message?.files?.indexOf(file) ?: 1) {
        message?.files?.size ?: 1
    }
    val lazyListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
    LaunchedEffect(key1 = show) {
        pagerState.scrollToPage(message?.files?.indexOf(file) ?: 1)
    }
    LaunchedEffect(key1 = pagerState.currentPage) {
        lazyListState.animateScrollToItem(pagerState.currentPage)
    }
    val scope = rememberCoroutineScope()
    var play by remember {
        mutableStateOf(false)
    }
    var changeProgress by remember {
        mutableLongStateOf(0L)
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
            if (pagerState.isScrollInProgress.not()) {
                playButtonShow = false
            }
        }
    }
    LaunchedEffect(key1 = pagerState.isScrollInProgress) {
        playButtonShow = if (pagerState.isScrollInProgress) {
            true
        } else {
            delay(500)
            false
        }
    }
    MediaPopUp(show = show, size = size, offset = offset, mediaContent = {
        MediaContent(
            message,
            pagerState,
            play,
            it,
            exoPlayer = exoPlayer,
            changeProgress,
            cacheDataSource
        ) { cProgress, cDuration, isPlay ->
            progress = cProgress
            duration = cDuration
            play = isPlay
        }
        Log.d("dsfdsf", "ChatMediaPopUp: $it")
    }, onClick = {
        playButtonShow = playButtonShow.not()
    }, overlyContent = {
        val context = LocalContext.current
        val imageLoader = Coil.imageLoader(context)
        val videoLoader = Coil.imageLoader(context).newBuilder()
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
                if (message?.files?.getOrNull(pagerState.currentPage)?.path?.getType()
                        ?.startsWith("video") == true
                ) {
                    PlayButton(
                        play = play,
                        onPlayRequest = { play = it },
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally)
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
                            state = lazyListState,
                            flingBehavior = flingBehavior,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.CenterHorizontally)
                        ) {
                            items(files.size) {
                                val currentFile = files[it]
                                val isVideo = currentFile.fromPath.startsWith("video")
                                val isMyFrom = message.from == CurrentUser.token
                                Box(
                                    modifier = Modifier.size(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ImagePreview(
                                        isVideo,
                                        isMyFrom,
                                        if (isVideo) videoLoader else imageLoader,
                                        context,
                                        currentFile,
                                        pagerState.currentPage == it
                                    ) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(it)
                                        }
                                    }
                                }
//                                if (it != files.size) {
//                                    Spacer(modifier = Modifier.width(4.dp))
//                                }
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
                            visible = files.getOrNull(pagerState.currentPage)?.path?.getType()
                                ?.startsWith("video") == true
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = progress.formatTime(), color = Color.White)
                                    Text(text = duration.formatTime(), color = Color.White)
                                }
                                Slider(
                                    value = progress.toFloat().coerceAtLeast(0f),
                                    onValueChange = {
                                        changeProgress = it.toLong()
                                    },
                                    valueRange = 0f..duration.toFloat().coerceAtLeast(0f),
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = Color.White,
                                        inactiveTrackColor = Color.White.copy(0.5f),
                                        thumbColor = Color.White,
                                    ),
                                )
                            }
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
    selected: Boolean,
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
                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                imageLoader.enqueue(request)
            } else {
                if (isMyFrom) {
                    bitmap = BitmapFactory.decodeFile(file.fromPath)
                }
                val request = ImageRequest.Builder(context).data(file.path)
                    .target { b -> bitmap = (b as BitmapDrawable).bitmap }.build()
                imageLoader.enqueue(request)
            }
        }

    }
    val plusSize by animateDpAsState(targetValue = if (selected) 8.dp else 0.dp, label = "")
    Box(
        modifier = Modifier
            .size(56.dp + plusSize, 56.dp)
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
    exoPlayer: ExoPlayer,
    progress: Long,
    cacheDataSource: CacheDataSource.Factory,
    onEvent: (Long, Long, Boolean) -> Unit
) {
    val context = LocalContext.current
    val imageLoader = Coil.imageLoader(context)
    if (message != null) {
        val isMyFrom = message.from == CurrentUser.token
        val files = message.files
        HorizontalPager(state = pagerState) {
            val file = files[it]
            if (file.path.getType().startsWith("video")) {
                VideoView(
                    isMyFrom,
                    file,
                    onEvent,
                    play,
                    progress,
                    exoPlayer,
                    cacheDataSource
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
    isMyFrom: Boolean,
    file: FileMessage,
    onEvent: (Long, Long, Boolean) -> Unit,
    play: Boolean,
    progress: Long,
    exoPlayer: ExoPlayer,
    cacheDataSource: CacheDataSource.Factory,
) {
    if (isMyFrom && File(file.fromPath).exists()) {
        VideoViewBasic(
            videoUri = Uri.parse(file.fromPath),
            changePosition = progress,
            onVideoEvent = { d, c, p ->
                onEvent.invoke(c, d, p)
            },
            play = play,
            exoplayer = exoPlayer
        )
    } else {
        VideoViewBasicWithSource(
            mediaSource = ProgressiveMediaSource.Factory(cacheDataSource).createMediaSource(
                MediaItem.fromUri(Uri.parse(file.path))
            ),
            changePosition = progress,
            onVideoEvent = { d, c, p ->
                onEvent.invoke(c, d, p)
            },
            play = play,
            exoplayer = exoPlayer
        )
    }
}
