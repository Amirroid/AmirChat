package ir.amirroid.amirchat.di

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object ExoModule {
    @Provides
    @Singleton
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Provides
    @Singleton
    fun provideDataSource(@ApplicationContext context: Context) = DefaultDataSource.Factory(context)


    @Provides
    @Singleton
    fun provideRender(@ApplicationContext context: Context): RenderersFactory =
        DefaultRenderersFactory(context)

    @Provides
    @Singleton
    fun provideTrackSelector(@ApplicationContext context: Context): TrackSelector =
        DefaultTrackSelector(context)

    @Provides
    @Singleton
    fun provideLoadControl(): LoadControl = DefaultLoadControl()

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes,
        loadControl: LoadControl,
        trackSelector: TrackSelector,
        renderersFactory: RenderersFactory
    ) = ExoPlayer.Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .setAudioAttributes(audioAttributes, true)
        .setLoadControl(loadControl)
        .setTrackSelector(trackSelector)
        .setRenderersFactory(renderersFactory)
        .setDeviceVolumeControlEnabled(true)
        .build()

    @Provides
    @Singleton
    fun provideCacheEvictor(): CacheEvictor = NoOpCacheEvictor()


    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context,
        cacheEvictor: CacheEvictor,

        ) = SimpleCache(context.cacheDir, cacheEvictor)

    @Provides
    @Singleton
    fun provideHttpSource() = DefaultHttpDataSource.Factory()

    @Provides
    @Singleton
    fun provideCacheDataSource(
        cache: SimpleCache,
        defaultHttpDataSource: DefaultHttpDataSource.Factory
    ) = CacheDataSource.Factory().apply {
        setCache(cache)
        setUpstreamDataSourceFactory(defaultHttpDataSource)
    }


}