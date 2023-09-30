package ir.amirroid.amirchat.utils

import android.Manifest
import android.app.Notification.MessagingStyle
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val job = Job()
    private val scope = CoroutineScope(job)

    @Inject
    lateinit var tokenHelper: TokenHelper

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            Log.e("dsfsfd", "onMessageReceived: start", )
            if (message.notification != null) {
                val user =
                    Gson().fromJson(message.data.toString(), UserModel::class.java) ?: UserModel()
                sendMessage(message.notification!!, user)
            }
        }catch (e:Exception){
            Log.e("dsfsfd", "onMessageReceived: ${e.message}", )
        }
    }

    private fun sendMessage(notification: RemoteMessage.Notification, user: UserModel) {
        val notification = NotificationCompat.Builder(this, Constants.NOTIF_ID)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setGroup(user.token)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(313, notification)
        }
    }

    override fun onNewToken(token: String) {
        scope.launch {
            val token = tokenHelper.user.firstOrNull()?.token
            if (CurrentUser.token != null) {
                val firestore = Firebase.firestore.collection(Constants.USERS)
                firestore.document(token ?: "").update(
                    mapOf(
                        "fcmToken" to token
                    )
                )
            }
        }
        super.onNewToken(token)
    }
}