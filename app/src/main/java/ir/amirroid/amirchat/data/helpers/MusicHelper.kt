package ir.amirroid.amirchat.data.helpers

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import ir.amirroid.amirchat.data.models.media.MediaModel
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MusicHelper @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val dataSource: DefaultDataSource.Factory,
    private val cacheDataSource: CacheDataSource.Factory
) {
    var onEvent: ((Int) -> Unit)? = null
    private val listener = object : Player.Listener {
        @Deprecated("Deprecated in Java")
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            onEvent?.invoke(playbackState)
            super.onPlayerStateChanged(playWhenReady, playbackState)
        }
    }

    init {
        exoPlayer.addListener(listener)
    }

    fun playWithOutNotification(uri: Uri) {
        exoPlayer.setMediaSource(
            ProgressiveMediaSource.Factory(dataSource).createMediaSource(MediaItem.fromUri(uri))
        )
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun play() {
        exoPlayer.play()
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun setVideos(videos: List<MediaModel>) {
        val videoMediaSource = videos.map {
            ProgressiveMediaSource.Factory(dataSource).createMediaSource(MediaItem.fromUri(it.uri))
        }
        val concatenatingMediaSource = ConcatenatingMediaSource()
        concatenatingMediaSource.addMediaSources(videoMediaSource)
        exoPlayer.setMediaSource(concatenatingMediaSource)
    }

    fun playIndex(index: Int) {
        exoPlayer.seekTo(index, 0)
        exoPlayer.prepare()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun observeToStates(onState: (Int) -> Unit) {
        onEvent = onState
    }

    fun clearObservers() {
        onEvent = null
        exoPlayer.removeListener(listener)
    }

    fun dispose() {
        exoPlayer.stop()
        exoPlayer.release()
    }

    fun seekTo(seek: Long) {
        exoPlayer.seekTo(seek)
    }

    fun getPosition() = exoPlayer.currentPosition
    fun playWithCache(path: String) {
        exoPlayer.setMediaSource(
            ProgressiveMediaSource.Factory(cacheDataSource).createMediaSource(MediaItem.fromUri(path.toUri()))
        )
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
        exoPlayer.play()
    }
}