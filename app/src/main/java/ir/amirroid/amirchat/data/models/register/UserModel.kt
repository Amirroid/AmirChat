package ir.amirroid.amirchat.data.models.register

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class UserModel(
    val token: String = "",
    val fcmToken: String = "",
    var mobileNumber: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var userId: String = "",
    var bio: String = "",
    var profilePictureUrl: String? = null,
)