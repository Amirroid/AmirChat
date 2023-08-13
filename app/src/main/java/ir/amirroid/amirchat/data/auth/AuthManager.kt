package ir.amirroid.amirchat.data.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.helpers.AppSignatureHelper
import ir.amirroid.amirchat.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext val context: Context,
    private val signatureHelper: AppSignatureHelper,
    private val okHttpClient: OkHttpClient
) {
    fun generateCode() = (10000..999999).random()
    fun generateHashCode() = signatureHelper.getSignatures().lastOrNull() ?: ""
    private fun sendCode(
        phone: String,
        code: String,
        hashCode: String
    ): String {
        val request = Request.Builder()
            .url(
                "http://api.payamak-panel.com/post/Send.asmx/SendByBaseNumber3?username=" +
                        Constants.USER_NAME +
                        "&password=" +
                        Constants.PASSWORD +
                        "&text=@$code@&to=$phone"
            )
            .build()
        val response = okHttpClient.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: "")
        return json.getString("ReturnValue") ?: ""
    }

}