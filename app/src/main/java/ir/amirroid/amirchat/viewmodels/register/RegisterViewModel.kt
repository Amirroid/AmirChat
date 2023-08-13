package ir.amirroid.amirchat.viewmodels.register

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.auth.AuthManager
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {
    var phoneNumber by mutableStateOf("")
    var verifyCode by mutableStateOf("")
    init {
        Log.d("riosfd", authManager.generateHashCode())
    }
}