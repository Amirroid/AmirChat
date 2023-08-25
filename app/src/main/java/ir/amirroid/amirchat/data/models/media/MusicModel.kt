package ir.amirroid.amirchat.data.models.media

import android.net.Uri

data class MusicModel(
    val name: String,
    val artistName: String,
    val data: String,
    val duration: Long,
    val id: Long,
    val uri: Uri,
)

data class MusicModelForJson(
    val name: String,
    val artistName: String,
    val data: String,
    val duration: Long,
    val id: Long,
    val uri: String,
)
