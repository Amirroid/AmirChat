package ir.amirroid.emojikeyboard2

data class EmojiData(
    val character: String,
    val codePoint: String,
    val group: String,
    val slug: String,
    val subGroup: String,
    val unicodeName: String,
    val variants: List<Variant>?
) {
    data class Variant(
        val character: String,
        val slug: String
    )
}