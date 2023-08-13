package ir.amirroid.amirchat.data.models.media

data class ContactModel(
    val id: Long,
    val name: String,
    val numbers: List<String>
)