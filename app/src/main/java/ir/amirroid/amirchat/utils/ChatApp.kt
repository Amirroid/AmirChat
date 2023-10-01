package ir.amirroid.amirchat.utils


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class ChatApp : Application() {
    @Inject
    lateinit var imageLoader: ImageLoader
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    Constants.NOTIF_ID,
                    "Amir Chat",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
        Coil.setImageLoader(
            imageLoader
        )
    }
}