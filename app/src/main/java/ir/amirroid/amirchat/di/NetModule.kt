package ir.amirroid.amirchat.di

import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.imageLoader
import coil.request.CachePolicy
import coil.util.Logger
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.LocalCacheSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetModule {

    @Provides
    fun provideOkHttp() = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .callTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideFirebase() = Firebase

    @Provides
    @Singleton
    fun provideStorage(
        firebase: Firebase
    ) = firebase.storage.reference

    @Provides
    @Singleton
    fun provideFirestore(
        firebase: Firebase
    ): FirebaseFirestore {
        val fireStore = firebase.firestore
        fireStore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        return fireStore
    }

    @Provides
    @Singleton
    fun provideDatabase(
        firebase: Firebase
    ) = firebase.database.reference

    @Provides
    @Singleton
    fun provideMessaging(
        firebase: Firebase
    ) = firebase.messaging


    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ) = ImageLoader.Builder(context)
        .respectCacheHeaders(false)
        .allowHardware(true)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .build()
}