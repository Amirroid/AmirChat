package ir.amirroid.amirchat.data.models.media

import android.net.Uri

data class MediaModel(
    val name: String,
    val data: String,
    val duration: Long,
    val id: Long,
    val uri:Uri,
    val dateAdded:Long
)


data class MediaConvertModel(
    val name: String,
    val data: String,
    val duration: Long,
    val id: Long,
    val uri:String,
    val dateAdded:Long
)
