package ir.amirroid.amirchat.data.models.register

data class UserModel(
    var token: String,
    var mobileNumber: String,
    var firstName: String,
    var lastName: String,
    var userId: String,
    var bio: String,
    var profilePictureUrl: String? = null,
)
