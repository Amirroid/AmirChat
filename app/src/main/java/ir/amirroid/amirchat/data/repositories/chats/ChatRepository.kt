package ir.amirroid.amirchat.data.repositories.chats

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.FileTypes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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
import ir.amirroid.amirchat.utils.getType
import ir.amirroid.amirchat.utils.id
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ChatRepository @Inject constructor(
    database: DatabaseReference,
    storage: StorageReference,
    private val localData: LocalData,
    @ApplicationContext val context: Context,
    private val sendingMessagesDao: SendingMessagesDao
) {
    private val rooms = database.child(Constants.ROOMS)
    private val chats = database.child(Constants.CHATS)
    private val status = database.child(Constants.USERS_STATUS)
    private val chatStorage = storage.child(Constants.CHATS)


    private val job = Job()
    private val scope = CoroutineScope(job)

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
                    scope.launch(Dispatchers.IO) { localData.setRooms(Gson().toJson(list)) }
                    onReceive.invoke(list)
                }
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

    fun addMessage(message: MessageModel, onFileResponse: (FileNetData) -> Unit) {
        scope.launch {
            sendingMessagesDao.add(message.copy(status = Constants.SENDING))
        }
        addFiles(message.files, onFileResponse) {
            chats.child(
                message.id
            ).setValue(message.copy(files = it)).addOnCompleteListener {
                scope.launch {
                    sendingMessagesDao.delete(message)
                }
            }
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
                    snapshot.ref.removeValue().addOnSuccessListener {
                        scope.launch {
                            localData.deleteRoom(room)
                            localData.deleteChats(room.id)
                        }
                    }
                }
            })
    }

    fun onDestroy() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
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
}