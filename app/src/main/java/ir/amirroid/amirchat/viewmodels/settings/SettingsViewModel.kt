package ir.amirroid.amirchat.viewmodels.settings

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.auth.AuthManager
import ir.amirroid.amirchat.data.models.register.CurrentUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authManager: AuthManager,
) : ViewModel() {
    var loading by mutableStateOf(false)
    var editMode by mutableStateOf(false)

    var user = CurrentUser.user

    var firstName by mutableStateOf(user?.firstName ?: "")
    var lastName by mutableStateOf(user?.lastName ?: "")
    var id by mutableStateOf(user?.userId ?: "")
    var bio by mutableStateOf(user?.bio ?: "")
    var image by mutableStateOf(user?.profilePictureUrl?.toUri())

    var idExists by mutableStateOf(false)


    fun logOut() = viewModelScope.launch(Dispatchers.IO) {
        authManager.logOut()
    }

    fun checkId() {
        if (id != user?.userId) {
            loading = true
            authManager.checkIdExist(id) {
                idExists = it
                loading = false
            }
        } else {
            idExists = false
        }
    }

    fun editUser(onEnd: () -> Unit) {
        editMode = false
        loading = true
        user?.let {
            authManager.editUser(
                it.copy(
                    firstName = firstName,
                    lastName = lastName,
                    bio = bio,
                    userId = id
                ),
                onEnd,
            )
        }
    }

    fun logIn(onComplete: () -> Unit) {
        loading = true
        editMode = false
        authManager.loginUser(
            user?.mobileNumber ?: "",
            image,
            id,
            firstName,
            lastName,
            bio,
        ) {
            loading = false
            onComplete.invoke()
        }
    }

}