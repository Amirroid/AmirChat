package ir.amirroid.amirchat.data.helpers

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays
import javax.inject.Inject

class AppSignatureHelper @Inject constructor(
    @ApplicationContext val context: Context
) : ContextWrapper(context) {
    fun getSignatures(): List<String> {
        val appCodes = mutableListOf<String>()
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in packageInfo.signatures) {
                val hashCode = getHashCode(signature)
                if (hashCode != null) {
                    appCodes += String.format("%s", hashCode)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return appCodes
    }

    private fun getHashCode(signature: Signature): String? {
        val appInfo = context.packageName + " " + signature.toString()
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(
                appInfo.toByteArray(
                    StandardCharsets.UTF_8
                )
            )
            val digest = messageDigest.digest()
            val digestRange = Arrays.copyOfRange(digest, 0, 9)
            val base64Hash =
                Base64.encodeToString(digestRange, Base64.NO_PADDING.or(Base64.NO_WRAP))
            return base64Hash.substring(0, 11)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    init {
        getSignatures()
    }
}