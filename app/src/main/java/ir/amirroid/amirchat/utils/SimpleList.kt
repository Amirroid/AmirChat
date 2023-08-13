package ir.amirroid.amirchat.utils

object SimpleList {
    val listProfiles = listOf(
        Profile("Amirreza"),
        Profile("09150211935"),
        Profile("Test")
    )

    val listMessages = listOf(
        "hello" to true,
        "hello, good morning" to false,
        "How are you" to false,
        "good, how are you" to true,
        "so so" to false,
    )
}


data class Profile(
    val name: String,
    val desc: String = "Hello, Good morning",
    val image: String = "https://www.alamto.com/wp-content/uploads/2023/05/flower-9.jpg"
)