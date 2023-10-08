package ir.amirroid.amirchat.data.models.register

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserModel(
    val token: String = "",
    var mobileNumber: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var userId: String = "",
    var bio: String = "",
    var profilePictureUrl: String? = null,
    val fcmToken: String = "",
):Parcelable {
    fun isSavedMessageUser() = token.isEmpty() || token == CurrentUser.token
}