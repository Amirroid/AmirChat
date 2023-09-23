package ir.amirroid.amirchat.viewmodels.register

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.auth.AuthManager
import ir.amirroid.amirchat.data.receivers.SMSBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class RegisterViewModel
@Inject constructor(
    private val authManager: AuthManager,
    @ApplicationContext val context: Context
) : ViewModel() {
    var phoneNumber by mutableStateOf("")
    var verifyCode by mutableStateOf("")
    var currentCode by mutableStateOf("")


    var imageProfile by mutableStateOf<Uri?>(null)
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var id = MutableStateFlow("")
    var idExist by mutableStateOf(false)
    var bio by mutableStateOf("")

    var loading by mutableStateOf(false)

    private val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    private val smsReceiver = SMSBroadcastReceiver()
    fun sendCode(onSend: () -> Unit) {
        loading = true
        currentCode = authManager.generateCode().toString()
        authManager.sendCode(
            phoneNumber,
            currentCode,
            authManager.generateHashCode()
        ) {
            loading = false
            onSend.invoke()
        }
//        onSend.invoke()
//        currentCode = "123456"
//        verifyCode = "123456"
    }

    fun startBroadCast() {
        smsReceiver.onReceive {
            verifyCode = it.split(":")[1].trim().substring(1, 7)
        }
        context.registerReceiver(smsReceiver, intentFilter)
        val client = SmsRetriever.getClient(context)
        client.startSmsRetriever().addOnCompleteListener {
            Log.d("gdfrgre", "startBroadCast: " + it.isSuccessful)
        }
    }

    fun unRegisterBroadCast() {
        context.unregisterReceiver(smsReceiver)
    }

    init {
        checkId()
    }

    @OptIn(FlowPreview::class)
    private fun checkId() = viewModelScope.launch(Dispatchers.IO) {
        id.debounce(500).collect {
            authManager.checkIdExist(id.value) {
                idExist = it
            }
        }
    }

    fun logIn(onComplete: () -> Unit) {
        loading = true
        authManager.loginUser(
            phoneNumber,
            imageProfile,
            id.value,
            firstName,
            lastName,
            bio,
        ) {
            loading = false
            onComplete.invoke()
        }
    }

    fun checkMobile(onCheck: (Boolean) -> Unit) {
        authManager.checkMobileExists(phoneNumber) {
            viewModelScope.launch {
                onCheck.invoke(
                    it
                )
            }
        }
    }

    fun logInWithMobile(onComplete: () -> Unit) {
        authManager.loginWithMobile(phoneNumber, onComplete)
    }
}