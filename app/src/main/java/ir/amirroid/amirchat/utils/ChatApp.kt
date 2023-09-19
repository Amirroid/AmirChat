package ir.amirroid.amirchat.utils


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class ChatApp : Application() {
    override fun onCreate() {
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
        super.onCreate()
    }
}