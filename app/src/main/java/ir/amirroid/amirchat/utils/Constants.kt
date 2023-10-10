package ir.amirroid.amirchat.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.media.SendDataType

object Constants {


    // back end
    const val BASE_UIL_FIREBASE_STORAGE = "https://firebasestorage.googleapis.com"
    const val API_KEY = "7c6c54d003bd422058d2c93dbd7140e2454c495f6f6a2699f24b927c12b0f1e3"
    const val API_KEY_CLOUD_MESSAGING = "AAAAALu-Vrs:APA91bFF5k6L3OvmDhOJc2jWtRIzr_K8iI_ll-L3MRq-3JF_0i1N0XZCVPG7ifG3P0CXHSB6PTrsTlnoWo6jC1W98IgtGPG2jeTIRj4D1ALZo_BYrvvZLrWtTf-QJqlEu35jpzcGagBJ "

    // types
    const val SENDING = 0
    const val SEND = 1
    const val SEEN = 2

    const val GALLERY = 1
    const val FILE = 2
    const val LOCATION = 3
    const val CONTACT = 4
    const val MUSIC = 5
    const val STICKER = 6


    const val TYPING = 1


    // pages
    const val REGISTER = "register"
    const val HOME = "home"
    const val SPLASH = "splash"
    const val SEARCH = "search"
    const val CHAT = "chat"
    const val PROFILE = "profile"
    const val QR_CODE = "qrcode"
    const val FORWARD = "forward"
    const val SETTINGS = "settings"
    const val CONTACTS = "contacts"

    // preferences
    const val CATCH_NAME = "catch"
    const val USER = "user"


    // references
    const val USERS_STATUS = "usersStatus"
    const val LAST_ONLINE = "lastOnline"
    const val USERS = "Users"
    const val ROOMS = "Rooms"
    const val CHATS = "Chats"
    const val ONLINE = "online"

    // database
    const val SENDING_MESSAGES = "sending_messages"

    // notifs
    const val NOTIF_ID = "AmirChat"

    val listMessageEmoji = listOf(
        getEmoji(0X1F44D),
        getEmoji(0X1F601),
        getEmoji(0X1F602),
        getEmoji(0X1F603),
        getEmoji(0X1F605),
        getEmoji(0X1F606),
        getEmoji(0X1F624),
        getEmoji(0X1F637),
        getEmoji(0X1F64C),
        getEmoji(0X1F64F),
    )


    val typesForSendData = listOf(
        SendDataType(
            getColor("#0c67c2"),
            R.string.gallery,
            R.drawable.round_image_24
        ),
        SendDataType(
            getColor("#0c9c11"),
            R.string.file,
            R.drawable.round_insert_drive_file_24
        ),
        SendDataType(
            getColor("#8ec20c"),
            R.string.location,
            R.drawable.baseline_location_on_24
        ),
        SendDataType(
            getColor("#9c990c"),
            R.string.contacts,
            R.drawable.round_person_24
        ),
        SendDataType(
            getColor("#9c0c54"),
            R.string.music,
            R.drawable.round_play_arrow_24
        ),
    )

    val randomBrush = listOf(
        Brush.linearGradient(
            listOf(
                getColor("#0c67c2"),
                getColor("#0a58a6"),

                ),
        ),
        Brush.linearGradient(
            listOf(
                getColor("#0c9c11"),
                getColor("#0b8a0f"),

                ),
        ),
        Brush.linearGradient(
            listOf(
                getColor("#8ec20c"),
                getColor("#80ad0c"),

                ),
        ),
        Brush.linearGradient(
            listOf(
                getColor("#ba0f3c"),
                getColor("#9c0c32"),

                ),
        )
    )
}