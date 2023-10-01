package ir.amirroid.amirchat.data.repositories.chats

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.FileTypes
import androidx.media3.common.util.HandlerWrapper.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.database.SendingMessagesDao
import ir.amirroid.amirchat.data.helpers.DownloadState
import ir.amirroid.amirchat.data.helpers.FileNetData
import ir.amirroid.amirchat.data.helpers.LocalData
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.data.models.chat.UserStatus
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.divIfNotZero
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.utils.getText
import ir.amirroid.amirchat.utils.getType
import ir.amirroid.amirchat.utils.id
import ir.amirroid.amirchat.viewmodels.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val database: DatabaseReference,
    storage: StorageReference,
    val localData: LocalData,
    @ApplicationContext val context: Context,
    private val sendingMessagesDao: SendingMessagesDao,
    private val okHttpClient: OkHttpClient
) {
    private val rooms = database.child(Constants.ROOMS)
    private val chats = database.child(Constants.CHATS)
    private val status = database.child(Constants.USERS_STATUS)
    private val chatStorage = storage.child(Constants.CHATS)

    val connecting = MutableStateFlow(true)

    private lateinit var scope: CoroutineScope

    fun setScope(scope1: CoroutineScope) {
        if (this::scope.isInitialized.not()) {
            this.scope = scope1
            scope.launch {
                localData.rooms.firstOrNull()?.let { roomJson ->
                    val rooms = Gson().fromJson(roomJson, Array<ChatRoom>::class.java).toList()
                    sendingMessagesDao.getAll().forEach { message ->
                        addMessage(
                            message.copy(status = Constants.SEND),
                            rooms.filter { model -> model.id == message.chatRoom }.firstOrNull()
                                ?.getUser()?.fcmToken ?: ""
                        ) {
                            ChatViewModel.uploadFiles.value =
                                ChatViewModel.uploadFiles.value.apply {
                                    this[it.url] = it
                                }
                        }
                    }
                }
            }
        } else {
            this.scope = scope1
        }
    }

    fun observeToRooms(
        onReceive: (List<ChatRoom>) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            localData.rooms.collectLatest {
                if (it != null) {
                    try {
                        val data = Gson().fromJson(it, Array<ChatRoom>::class.java)
                        onReceive.invoke(data.toList())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        rooms.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map { it.getValue(ChatRoom::class.java) ?: return }.apply {
                    val list =
                        filter { it.from.token == CurrentUser.token || it.to.token == CurrentUser.token }
                    scope.launch(Dispatchers.IO) {
                        if (isActive) {
                            localData.setRooms(Gson().toJson(list))
                        }
                    }
                    onReceive.invoke(list)
                }
                connecting.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    fun addRoom(
        room: ChatRoom
    ) {
        rooms.child(room.to.userId + "-" + room.from.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) = Unit
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists().not()) {
                        rooms.child(
                            room.id
                        ).setValue(room)
                    }
                }
            })
    }


    fun addRoomWithUser(
        user: UserModel, onComplete: (room: ChatRoom) -> Unit
    ) {
        rooms.child(CurrentUser.token + "-" + user.token)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) = Unit
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists().not()) {
                        rooms.child(user.token + CurrentUser.user?.token)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) = Unit
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists().not()) {
                                        val chatRoom = ChatRoom(
                                            CurrentUser.user ?: UserModel(),
                                            user,
                                        )
                                        rooms.child(CurrentUser.token + "-" + user.token).setValue(
                                            chatRoom
                                        ).addOnSuccessListener {
                                            onComplete.invoke(chatRoom)
                                        }
                                    } else {
                                        val chatRoom = ChatRoom(
                                            user,
                                            CurrentUser.user ?: UserModel(),
                                        )
                                        onComplete.invoke(chatRoom)
                                    }
                                }
                            })
                    } else {
                        val chatRoom = ChatRoom(
                            CurrentUser.user ?: UserModel(),
                            user,
                        )
                        onComplete.invoke(chatRoom)
                    }
                }
            })
    }

    fun observeToChat(room: String, onChat: (List<MessageModel>) -> Unit) {
        scope.launch {
            localData.collectToChatsFromRoom(room, onChat)
        }
        chats.orderByChild("chatRoom").equalTo(room)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) = Unit
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.map {
                        it.getValue(MessageModel::class.java) ?: MessageModel()
                    }.let {
                        scope.launch {
                            localData.setChatsFromRoom(room, it)
                        }
                        onChat.invoke(it)
                    }
                }
            })
    }

    fun addMessage(message: MessageModel, fcmToken: String, onFileResponse: (FileNetData) -> Unit) {
        scope.launch {
            sendingMessagesDao.add(message.copy(status = Constants.SENDING))
        }
        addFiles(message.files, onFileResponse) {
            chats.child(
                message.id
            ).setValue(message.copy(files = it)).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendFcmMessage(fcmToken, message)
                    chats.orderByChild("chatRoom").equalTo(message.chatRoom).get()
                        .addOnSuccessListener { chatTask ->
                            val children =
                                chatTask.children.map { model -> model.getValue(MessageModel::class.java) }
                            Log.d("fdfds", "addMessage: ${children.size}")
                            val fromCount = children.count { model ->
                                model?.from == message.chatRoom.split("-").firstOrNull()
                            }
                            val toCount = children.size - fromCount
                            rooms.child(message.chatRoom).updateChildren(
                                mapOf(
                                    "lastMessage" to message.getText(context),
                                    "lastTime" to System.currentTimeMillis(),
                                    "fromNumberOfMessages" to fromCount,
                                    "toNumberOfMessages" to toCount
                                )
                            )
                        }
                }
                scope.launch {
                    sendingMessagesDao.delete(message)
                }
            }
        }
    }

    private fun sendFcmMessage(fcmToken: String, message: MessageModel) {
        scope.launch {
            val roomsJson = localData.rooms.firstOrNull() ?: "[]"
            val rooms = Gson().fromJson(roomsJson, Array<ChatRoom>::class.java).toList()
            val room = rooms.find { it.id == message.chatRoom }
            val isNotification = if (room?.from?.token == CurrentUser.token) {
                room?.toNotificationEnabled
            } else {
                room?.fromNotificationEnabled
            }
            if (isNotification == false) {
                Log.d("dsfsfd", "sendFcmMessage: $isNotification")
                return@launch
            }
            val user = if (room?.from?.token == message.from) {
                room.from
            } else {
                room?.to
            }
            val userJson = Gson().toJson(user)
            val json = """
        {
            "to": "$fcmToken",
            "notification": {
                "title": "${user?.getName()}",
                "body": "${message.message}"
            }
            "data" : $userJson
        }
    """.trimIndent()

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = RequestBody.create(mediaType, json)

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization", "key=${Constants.API_KEY_CLOUD_MESSAGING}")
                .build()
            okHttpClient.newCall(
                request
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("dsfsfd", "sendFcmMessage: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("dsfsfd", "sendFcmMessage: ${response.body?.string()}")
                }
            })
        }
    }

    fun getAllSending(room: String) = sendingMessagesDao.getAll(room)

    private fun addFiles(
        list: List<FileMessage>,
        onFileResponse: (FileNetData) -> Unit,
        onEnd: (
            List<FileMessage>
        ) -> Unit
    ) {
        if (list.firstOrNull()?.type == Constants.CONTACT || list.firstOrNull()?.type == Constants.LOCATION || list.firstOrNull()?.type == Constants.STICKER) {
            onEnd.invoke(list)
        } else {
            val links = mutableListOf<FileMessage>()
            if (list.isNotEmpty()) {
                list.forEachIndexed { index, file ->
                    Log.d("DSf", "addFiles: ${file.path}")
                    val name = System.currentTimeMillis().toString() + file.path.split("/").last()
                    val ref = chatStorage.child(name)
                    ref.putFile(File(file.path).toUri()).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { link ->
                            val fileMessage = file.copy(path = link.toString(), reference = name)
                            links.add(fileMessage)
                            if (index == list.size.minus(1)) {
                                onEnd.invoke(links)
                            }
                            onFileResponse.invoke(
                                FileNetData(
                                    file.fromPath,
                                    1f,
                                    DownloadState.SUCCESS
                                )
                            )
                        }
                    }.addOnFailureListener {
                        Log.d("DSf", "addFiles: ${it.localizedMessage}")
                        if (index == list.size.minus(1)) {
                            onEnd.invoke(links)
                        }
                        onFileResponse.invoke(
                            FileNetData(
                                file.fromPath,
                                -1f,
                                DownloadState.ERROR
                            )
                        )
                    }.addOnProgressListener {
                        val progress =
                            it.bytesTransferred.toFloat().divIfNotZero(it.totalByteCount.toFloat())
                        onFileResponse.invoke(
                            FileNetData(
                                file.fromPath,
                                progress,
                                DownloadState.IN_PROGRESS
                            )
                        )
                    }
                }
            } else {
                onEnd.invoke(links)
            }
        }
    }

    fun deleteRoom(room: ChatRoom) {
        rooms.child(room.id).removeValue()
        chats.orderByChild("chatRoom").equalTo(room.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) = Unit
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        it.getValue(MessageModel::class.java)?.let { message ->
                            message.files.forEach { file ->
                                chatStorage.child(file.reference).delete()
                            }
                        }
                    }
                    snapshot.children.forEach {
                        it.ref.removeValue()
                    }
                    scope.launch {
                        localData.deleteRoom(room)
                        localData.deleteChats(room.id)
                    }
                }
            })
    }

    fun onDestroy() {
        database.database.goOffline()
    }

    fun setEmoji(id: String, emoji: String?, from: Boolean) {
        chats.child(id).apply {
            if (from) {
                child(
                    "fromEmoji"
                ).setValue(emoji)
            } else {

                child(
                    "toEmoji"
                ).setValue(emoji)
            }
        }
    }

    fun deleteChats(messages: List<MessageModel>) {
        messages.forEach {
            chats.child(it.id).removeValue()
            it.files.forEach { file ->
                chatStorage.child(file.reference).delete()
            }
        }
    }

    fun observeToStatus(token: String, onValueChanged: (UserStatus) -> Unit) {
        status.child(token).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(UserStatus::class.java)?.let {
                    onValueChanged.invoke(
                        it
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit

        })
    }

    fun setStatus(userStatus: UserStatus) {
        status.child(CurrentUser.token ?: "").setValue(userStatus)
    }

    fun observeToNumberOfMessages(rooms: List<ChatRoom>, onData: (Map<String, Int>) -> Unit) {
        val numbers = HashMap<String, Int>()
        scope.launch {
            rooms.forEach { room ->
                localData.collectToChatsFromRoom(room.id) {
                    val number = if (room.from.token == CurrentUser.token) {
                        Log.d("dsfjdoif", "to ${room.toNumberOfMessages}")
                        room.toNumberOfMessages
                    } else {
                        Log.d("dsfjdoif", "from ${room.fromNumberOfMessages}")
                        room.fromNumberOfMessages
                    }
                    numbers[room.id] = (number - it.toMutableList().apply {
                        removeIf { model ->
                            model.status != Constants.SEEN || model.from == CurrentUser.token
                        }
                    }.count()).coerceAtLeast(0)
                    Log.d(
                        "dsfjdoif",
                        "observeToNumberOfMessages: ${room.lastMessage} $number ${numbers[room.id]} ${
                            it.toMutableList().apply {
                                removeIf { model ->
                                    model.status != Constants.SEEN || model.from == CurrentUser.token
                                }
                            }.count()
                        }"
                    )
                    onData.invoke(numbers)
                }
            }
        }
    }

    fun setSeen(id: String) {
        chats.child(id).child("status").setValue(Constants.SEEN)
    }

    fun setMyNotificationEnabled(enabled: Boolean, room: ChatRoom) {
        if (room.from.token == CurrentUser.token) {
            rooms.child(room.id).child("fromNotificationEnabled").setValue(enabled)
        } else {
            rooms.child(room.id).child("toNotificationEnabled").setValue(enabled)
        }
    }

    fun editMessage(text: String, id: String) {
        chats.child(id).child("message").setValue(text)
    }

    fun addMessageWithOutFiles(
        message: MessageModel,
        fcmToken: String,
        onResponse: () -> Unit
    ) {
        scope.launch {
            sendingMessagesDao.add(message.copy(status = Constants.SENDING))
        }
        chats.child(
            message.id
        ).setValue(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendFcmMessage(fcmToken, message)
                chats.orderByChild("chatRoom").equalTo(message.chatRoom).get()
                    .addOnSuccessListener { chatTask ->
                        val children =
                            chatTask.children.map { model -> model.getValue(MessageModel::class.java) }
                        Log.d("fdfds", "addMessage: ${children.size}")
                        val fromCount = children.count { model ->
                            model?.from == message.chatRoom.split("-").firstOrNull()
                        }
                        val toCount = children.size - fromCount
                        rooms.child(message.chatRoom).updateChildren(
                            mapOf(
                                "lastMessage" to message.getText(context),
                                "lastTime" to System.currentTimeMillis(),
                                "fromNumberOfMessages" to fromCount,
                                "toNumberOfMessages" to toCount
                            )
                        ).addOnSuccessListener {
                            onResponse.invoke()
                        }
                    }
            }
            scope.launch {
                sendingMessagesDao.delete(message)
            }
        }
    }

    fun deleteChats(id: String) {
        chats.orderByChild("chatRoom").equalTo(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val files = it.getValue(MessageModel::class.java)?.files
                        files?.forEach { file ->
                            chatStorage.child(file.reference).delete()
                        }
                        it.ref.removeValue()
                    }
                    rooms.child(id).updateChildren(
                        mapOf(
                            "lastTime" to 0,
                            "lastMessage" to ""
                        )
                    )
                    scope.launch {
                        localData.deleteChats(id)
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit

            })
    }

    fun markAsRead(id: String) {
        chats.orderByChild("chatRoom").equalTo(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.filter {
                        val message = it.getValue(MessageModel::class.java)
                        message?.from != CurrentUser.token && message?.status != Constants.SEEN
                    }.forEach {
                        it.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit

            })
    }
}