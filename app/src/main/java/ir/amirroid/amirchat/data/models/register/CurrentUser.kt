package ir.amirroid.amirchat.data.models.register

object CurrentUser {
    var token: String? = null
        private set
    var user: UserModel? = null
        private set


    fun setUser(user: UserModel) {
        this.token = user.token
        this.user = user
    }
}