package ir.amirroid.amirchat.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.media.SendDataType

object Constants {
    // back end
    const val BACK_END_APPLICATION_API_KEY = "2DE331E8-AE2F-46D7-8540-F29B6E80B7A1"
    const val BACK_END_APPLICATION_ID = "9B1E7FCF-E19D-C483-FFDA-121C70B00E00"
    const val USER_NAME = "09155415832"
    const val PASSWORD = "2m47a"


    // pages
    const val REGISTER = "register"
    const val HOME = "home"
    const val SEARCH = "search"
    const val CHAT = "chat"
    const val PROFILE = "profile"
    const val QR_CODE = "qrcode"

    // preferences
    const val CATCH_NAME = "catch"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val TOKEN = "token"


    val listMessageEmoji = listOf(
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