package ir.amirroid.amirchat.utils

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ir.amirroid.amirchat.data.helpers.LocalData
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.register.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {
    private val job = Job()
    private val scope = CoroutineScope(job)

    override fun onNewToken(token: String) {
        val localData = TokenHelper(
            this
        )
        scope.launch {
            CurrentUser.setToken(localData.token.firstOrNull())
            if (CurrentUser.token != null) {
                val firestore = Firebase.firestore.collection(Constants.USERS)
                firestore.document(CurrentUser.token ?: "").update(
                    mapOf(
                        "fcmToken" to token
                    )
                )
            }
        }
        super.onNewToken(token)
    }
}