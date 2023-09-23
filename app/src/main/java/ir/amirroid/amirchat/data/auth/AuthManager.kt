package ir.amirroid.amirchat.data.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.helpers.AppSignatureHelper
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    storage: StorageReference,
    private val messaging: FirebaseMessaging,
    database: DatabaseReference
) {

    private val userDatabase = fireStore.collection(Constants.USERS)
    private val userStorage = storage.child(Constants.USERS)
    private val usersStatus = database.child(Constants.USERS_STATUS)


    private val job = Job()
    private val scope = CoroutineScope(job)

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
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                Log.d("api_result", "sendCode: $code")
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
                        val model =
                            UserModel(
                                token, phone, firstName, lastName, id, bio, downloadUri.toString()
                            )
                        userDatabase.document(token).set(
                            model
                        ).addOnSuccessListener {
                            scope.launch {
                                CurrentUser.setUser(model)
                                tokenHelper.apply {
                                    setUserModel(model)
                                    startOnline()
                                    withContext(Dispatchers.Main){
                                        onComplete.invoke()
                                    }
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
            val model =
                UserModel(
                    token, phone, firstName, lastName, id, bio, downloadUri
                )
            userDatabase.add(
                model
            ).addOnSuccessListener {
                CurrentUser.setUser(model)
                scope.launch {
                    tokenHelper.apply {
                        setUserModel(model)
                        startOnline()
                        withContext(Dispatchers.Main){
                            onComplete.invoke()
                        }
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
        onComplete: () -> Unit
    ) {

        userDatabase.whereEqualTo("mobileNumber", mobileNumber).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val model = it.result.first().toObject(UserModel::class.java)
                scope.launch {
                    tokenHelper.apply {
                        setUserModel(model)
                        startOnline()
                        withContext(Dispatchers.Main){
                            onComplete.invoke()
                        }
                    }
                }
            } else onComplete.invoke()
        }
    }

    fun getMyUser() {
        userDatabase.document(CurrentUser.token ?: "").get().addOnSuccessListener {
            it.toObject(UserModel::class.java)?.let { user ->
                CurrentUser.setUser(user)
                scope.launch {
                    tokenHelper.setUserModel(user)
                }
            }
            messaging.token.addOnSuccessListener { token ->
                userDatabase.document(CurrentUser.token ?: "").update(
                    mapOf(
                        "fcmToken" to token
                    )
                )
            }
        }
    }

    init {
        startOnline()
    }

    private fun startOnline() {
        val children = hashMapOf<String, Any>(
            Constants.ONLINE to true,
        )
        usersStatus.child(CurrentUser.token ?: "").updateChildren(children)
        usersStatus.child(CurrentUser.token ?: "").onDisconnect().updateChildren(
            mapOf(
                Constants.ONLINE to false,
                Constants.LAST_ONLINE to System.currentTimeMillis(),
                "toToken" to null
            )
        )
    }
}