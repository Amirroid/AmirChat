package ir.amirroid.amirchat.data.models.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileMessage(
    val fromPath: String = "",
    val path: String = "",
    val type: Int = -1,
    val data: String = "",
    val reference:String = ""
):Parcelable