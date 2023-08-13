package ir.amirroid.amirchat.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@UnstableApi @Module
@InstallIn(SingletonComponent::class)
object ExoModule {
    @Provides
    @Singleton
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ) = ExoPlayer.Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .setAudioAttributes(audioAttributes, true)
        .setDeviceVolumeControlEnabled(true)
        .build()

    @Provides
    @Singleton
    fun provideDataSource(@ApplicationContext context: Context) = DefaultDataSource.Factory(context)
}