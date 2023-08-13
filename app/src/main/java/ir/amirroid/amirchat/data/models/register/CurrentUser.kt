package ir.amirroid.amirchat.data.models.register

object CurrentUser {
    var firstName: String? = null
        private set
    var lastName: String? = null
        private set
    var token: String? = null
        private set

    fun setFirstName(
        firstName: String,
    ) {
        this.firstName = firstName
    }
    fun setLastName(
        lastName: String,
    ) {
        this.lastName = lastName
    }
    fun setToken(
        token: String,
    ) {
        this.token = token
    }
}