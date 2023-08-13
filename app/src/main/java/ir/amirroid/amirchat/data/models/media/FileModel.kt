package ir.amirroid.amirchat.data.models.media

data class FileModel(
    val name: String,
    val data: String,
    val mimeType: String,
    val size: Long,
    val id: Long
)
