package ir.amirroid.amirchat.utils

sealed class ChatPages(val route: String) {
    object RegisterScreen : ChatPages(Constants.REGISTER)
    object HomeScreen : ChatPages(Constants.HOME)
    object SearchScreen : ChatPages(Constants.SEARCH)
    object ChatScreen : ChatPages(Constants.CHAT)
    object ProfileScreen : ChatPages(Constants.PROFILE)
    object QrCodeProfileScreen : ChatPages(Constants.QR_CODE)
}