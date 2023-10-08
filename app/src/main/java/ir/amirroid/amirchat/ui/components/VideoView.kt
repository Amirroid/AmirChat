package ir.amirroid.amirchat.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay


@UnstableApi
@Composable
fun VideoViewBasic(
    videoUri: Uri,
    changePosition: Long,
    onVideoEvent: (
        duration: Long,
        currentPosition: Long,
        play: Boolean
    ) -> Unit,
    play: Boolean = true,
    playPause: Boolean = false,
    modifier: Modifier = Modifier,
    context : Context = LocalContext.current,
    exoplayer: ExoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
    }
) {
    val listener = remember {
        object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                onVideoEvent.invoke(
                    exoplayer.duration,
                    exoplayer.currentPosition,
                    exoplayer.isPlaying
                )
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    exoplayer.seekTo(0L)
                    exoplayer.playWhenReady = false
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("voisduwi", "onPlayerError: ${error.message}")
                super.onPlayerError(error)
            }
        }
    }
    LaunchedEffect(key1 = changePosition) {
        exoplayer.seekTo(changePosition)
    }
    LaunchedEffect(key1 = Unit) {
        repeat(Int.MAX_VALUE) {
            onVideoEvent.invoke(
                exoplayer.duration,
                exoplayer.currentPosition,
                exoplayer.isPlaying
            )
            delay(490)
        }
    }
    LaunchedEffect(key1 = play) {
        if (play) {
            exoplayer.play()
        } else exoplayer.pause()
    }
    DisposableEffect(key1 = Unit) {
        exoplayer.setMediaItem(MediaItem.fromUri(videoUri))
        exoplayer.prepare()
        exoplayer.addListener(listener)
        onDispose {
            exoplayer.apply {
                removeListener(listener)
                stop()
            }
        }
    }
    AndroidView(factory = {
        val playerView = PlayerView(it)
        playerView.apply {
            player = exoplayer
            useController = false
            hideController()
        }
        playerView
    }, modifier = modifier.pointerInput(Unit) {
        if (playPause) {
            detectTapGestures {
                if (exoplayer.isPlaying) {
                    exoplayer.pause()
                } else exoplayer.play()
            }
        }
    })
}

@UnstableApi
@Composable
fun VideoViewBasicWithSource(
    mediaSource:ProgressiveMediaSource,
    changePosition: Long,
    onVideoEvent: (
        duration: Long,
        currentPosition: Long,
        play: Boolean
    ) -> Unit,
    play: Boolean = true,
    playPause: Boolean = false,
    modifier: Modifier = Modifier,
    context : Context = LocalContext.current,
    exoplayer: ExoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
    }
) {
    val listener = remember {
        object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                onVideoEvent.invoke(
                    exoplayer.duration,
                    exoplayer.currentPosition,
                    exoplayer.isPlaying
                )
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    exoplayer.seekTo(0L)
                    exoplayer.playWhenReady = false
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("voisduwi", "onPlayerError: ${error.message}")
                super.onPlayerError(error)
            }
        }
    }
    LaunchedEffect(key1 = changePosition) {
        exoplayer.seekTo(changePosition)
    }
    LaunchedEffect(key1 = Unit) {
        repeat(Int.MAX_VALUE) {
            onVideoEvent.invoke(
                exoplayer.duration,
                exoplayer.currentPosition,
                exoplayer.isPlaying
            )
            delay(490)
        }
    }
    LaunchedEffect(key1 = play) {
        if (play) {
            exoplayer.play()
        } else exoplayer.pause()
    }
    DisposableEffect(key1 = Unit) {
        exoplayer.setMediaSource(mediaSource)
        exoplayer.prepare()
        exoplayer.addListener(listener)
        onDispose {
            exoplayer.apply {
                removeListener(listener)
                stop()
            }
        }
    }
    AndroidView(factory = {
        val playerView = PlayerView(it)
        playerView.apply {
            player = exoplayer
            useController = false
            hideController()
        }
        playerView
    }, modifier = modifier.pointerInput(Unit) {
        if (playPause) {
            detectTapGestures {
                if (exoplayer.isPlaying) {
                    exoplayer.pause()
                } else exoplayer.play()
            }
        }
    })
}