package ir.amirroid.amirchat.data.models.chat

import android.content.Context
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.utils.formatDateTime
import ir.amirroid.amirchat.utils.getTextForStatus

data class UserStatus(
    var online: Boolean = false,
    var lastOnline: Long = System.currentTimeMillis(),
    var toToken: String? = null,
    var statusTo: Int? = null
) {
    fun getText(context: Context): String {
        return if (toToken == CurrentUser.token && statusTo != null) {
            getTextForStatus(statusTo ?: 1, context)
        } else {
            if (online) context.getString(
                R.string.online
            ) else context.getString(R.string.last_ofline_at) + lastOnline.formatDateTime()
        }
    }
}