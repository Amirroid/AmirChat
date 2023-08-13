package ir.amirroid.amirchat.ui.components

import android.media.session.PlaybackState
import android.net.Uri
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
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoplayer = remember {
        ExoPlayer.Builder(context)
            .build()
    }
    val listener = remember {
        object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                onVideoEvent.invoke(
                    exoplayer.duration,
                    exoplayer.currentPosition,
                    exoplayer.isPlaying
                )
                if (playbackState == ExoPlayer.STATE_ENDED){
                    exoplayer.seekTo(0L)
                    exoplayer.playWhenReady = false
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
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
            delay(500)
        }
    }
    DisposableEffect(key1 = Unit) {
        exoplayer.setMediaItem(MediaItem.fromUri(videoUri))
        exoplayer.prepare()
        exoplayer.addListener(listener)
        onDispose {
            exoplayer.apply {
                removeListener(listener)
                stop()
                release()
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
        detectTapGestures {
            if (exoplayer.isPlaying) {
                exoplayer.pause()
            } else exoplayer.play()
        }
    })
}