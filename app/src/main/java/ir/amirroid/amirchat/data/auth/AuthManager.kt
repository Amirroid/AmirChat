package ir.amirroid.amirchat.data.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.helpers.AppSignatureHelper
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext val context: Context,
    private val signatureHelper: AppSignatureHelper,
    private val okHttpClient: OkHttpClient,
    private val tokenHelper: TokenHelper,
    fireStore: FirebaseFirestore,
    storage: StorageReference
) {

    private val userDatabase = fireStore.collection(Constants.USERS)
    private val userStorage = storage.child(Constants.USERS)
    fun generateCode() = (100000..999999).random()
    fun generateHashCode() = signatureHelper.getSignatures().lastOrNull() ?: ""
    fun sendCode(
        phone: String,
        code: String,
        hashCode: String,
        onResponse: () -> Unit
    ) {
        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val body = RequestBody.create(
            mediaType,
            "receptor=$phone&template=AmirChat&type=1&param1=$code&param2=$hashCode"
        )
        val request = Request.Builder()
            .url("https://api.ghasedak.me/v2/verification/send/simple")
            .post(body)
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .addHeader("apikey", Constants.API_KEY)
            .addHeader("cache-control", "no-cache")
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                Log.d("api_result", "sendCode: $body")
                onResponse.invoke()
            }
        })
    }

    fun checkIdExist(id: String, onCheck: (Boolean) -> Unit) {
        userDatabase.whereEqualTo("userId", id).get().addOnCompleteListener {
            if (it.isSuccessful) {
                onCheck.invoke(it.result.isEmpty.not())
            } else onCheck.invoke(false)
        }
    }

    fun loginUser(
        phone: String,
        image: Uri?,
        id: String,
        firstName: String,
        lastName: String,
        bio: String,
        scope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        val token = tokenHelper.generateToken()
        if (image != Uri.EMPTY && image != null) {
            val imagePath = "$token.jpg"
            val ref = userStorage.child(imagePath)
            val fis = context.contentResolver.openInputStream(image) ?: return
            ref.putStream(fis).addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        userDatabase.add(
                            UserModel(
                                token, phone, firstName, lastName, id, bio, downloadUri.toString()
                            )
                        ).addOnSuccessListener {
                            scope.launch {
                                tokenHelper.apply {
                                    setToken(token)
                                    setFirstName(firstName)
                                    setLastName(lastName)
                                    setImage(downloadUri.toString())
                                    setMobile(phone)
                                    onComplete.invoke()
                                }
                            }
                        }
                    }
                } else {
                    it.exception?.printStackTrace()
                }
            }
        } else {
            val downloadUri = ""
            userDatabase.add(
                UserModel(
                    token, phone, firstName, lastName, id, bio, downloadUri
                )
            ).addOnSuccessListener {
                scope.launch {
                    tokenHelper.apply {
                        setToken(token)
                        setFirstName(firstName)
                        setLastName(lastName)
                        setMobile(phone)
                        onComplete.invoke()
                    }
                }
            }
        }
    }

    fun checkMobileExists(mobile: String, onCheck: (Boolean) -> Unit) {
        userDatabase.whereEqualTo("mobileNumber", mobile).get().addOnCompleteListener {
            if (it.isSuccessful) {
                onCheck.invoke(it.result.isEmpty.not())
            } else onCheck.invoke(false)
        }
    }

    fun loginWithMobile(
        mobileNumber: String,
        scope: CoroutineScope,
        onComplete: () -> Unit
    ) {

        userDatabase.whereEqualTo("mobileNumber", mobileNumber).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val model = it.result.first().data
                scope.launch {
                    tokenHelper.apply {
                        setToken(model["token"].toString())
                        setFirstName(model["firstName"].toString())
                        setLastName(model["lastName"].toString())
                        setImage(model["profilePictureUrl"].toString())
                        setMobile(mobileNumber)
                        onComplete.invoke()
                    }
                }
            } else onComplete.invoke()
        }
    }
}