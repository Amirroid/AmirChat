package ir.amirroid.amirchat.data.models.register

object CurrentUser {
    var token: String? = null
        private set
    var mobile: String? = null
        private set
    fun setToken(
        token: String,
    ) {
        this.token = token
    }
    fun setMobile(
        mobile: String,
    ) {
        this.mobile = mobile
    }
}