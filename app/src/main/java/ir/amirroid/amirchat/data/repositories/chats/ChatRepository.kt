package ir.amirroid.amirchat.data.repositories.chats

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.utils.Constants
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val database: DatabaseReference
) {
    private val rooms = database.child(Constants.ROOMS)
    private val chats = database.child(Constants.CHATS)
    fun observeToRooms(
        onReceive: (List<ChatRoom>) -> Unit
    ) {
        rooms.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map { it.getValue(ChatRoom::class.java) ?: return }
                    .apply(onReceive)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
    fun addRoom(
        room : ChatRoom
    ){
        rooms.child(

        )
    }
}