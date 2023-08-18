package ir.amirroid.amirchat.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSBroadcastReceiver : BroadcastReceiver() {
    private var onReceive: ((String) -> Unit)? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status?
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE) ?: ""
                    onReceive?.invoke(message)
                }
            }
        }
    }

    fun onReceive(callback: (String) -> Unit) {
        onReceive = callback
    }
}