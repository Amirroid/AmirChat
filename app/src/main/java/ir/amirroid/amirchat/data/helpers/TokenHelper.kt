package ir.amirroid.amirchat.data.helpers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random


class TokenHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val preferences = context.preferences
    private fun generateRandomString() = Random.nextLong().toString(36).substring(1)
    val job = Job()
    val scope = CoroutineScope(job)
    fun generateToken(): String {
        var token = ""
        while (token.length < 72) {
            token += generateRandomString()
        }
        return token.replace("]", "S").replace("[", "J").replace(".", "F").replace("#", "V")
            .replace("$", "D")
    }

    object Keys {
        val user = stringPreferencesKey(Constants.USER)
    }


    val user = preferences.data.map {
        val data = it[Keys.user]
        if (data == null) {
            null
        } else {
            Gson().fromJson(data, UserModel::class.java)
        }
    }

    init {
        initializeApp()
    }

    private fun initializeApp() = scope.launch(Dispatchers.IO) {
        launch {
            user.collectLatest {
                it?.let {
                    CurrentUser.setUser(it)
                }
            }
        }
    }

    suspend fun setUserModel(user: UserModel) {
        preferences.edit {
            it[Keys.user] = Gson().toJson(user)
        }
    }

    suspend fun logOut() {
        preferences.edit {
            it.remove(Keys.user)
        }
    }
}