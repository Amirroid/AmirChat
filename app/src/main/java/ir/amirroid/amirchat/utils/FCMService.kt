package ir.amirroid.amirchat.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.helpers.LocalData
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.register.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val job = Job()
    private val scope = CoroutineScope(job)

    @Inject
    lateinit var tokenHelper: TokenHelper

    @Inject
    lateinit var localData: LocalData

    override fun onMessageReceived(message: RemoteMessage) {
        var chats = mutableListOf<MessageModel>()
        scope.launch {
            val token = tokenHelper.user.firstOrNull()?.token
            localData.apply {
                val roomsModel = Gson().fromJson(rooms.firstOrNull(), Array<ChatRoom>::class.java)
                roomsModel.forEach {
                    getChats(it.id).let { messages ->
                        chats.addAllIf(messages.filter { f -> f.from != CurrentUser.token }) { model ->
                            contains(model)
                        }
                    }
                }
                val messagingStyle = MessagingStyle(Person.Builder().build())
                messagingStyle.conversationTitle = getString(R.string.app_name)
                chats.forEach { message ->
                    val room = roomsModel.firstOrNull { it.id == message.id }
                    val user = if (room?.from?.token == message.from) {
                        room.from
                    } else {
                        room?.to
                    }
                    val userName = user?.getName() ?: ""
                    val messageStyle = MessagingStyle.Message(
                        message.getText(this@FCMService),
                        message.date,
                        userName
                    )
                    messagingStyle.addMessage(messageStyle)
                }
                val notification = NotificationCompat.Builder(this@FCMService)
                    .setSubText(getString(R.string.app_name))
                    .setStyle(messagingStyle)
                    .build()
                if (ActivityCompat.checkSelfPermission(
                        this@FCMService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    NotificationManagerCompat.from(this@FCMService).notify(
                        313,
                        notification
                    )
                }
            }
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