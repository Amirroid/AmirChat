package ir.amirroid.amirchat.data.helpers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.models.register.CurrentUser
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
        val firstName = stringPreferencesKey(Constants.FIRST_NAME)
        val lastName = stringPreferencesKey(Constants.LAST_NAME)
        val token = stringPreferencesKey(Constants.TOKEN)
        val image = stringPreferencesKey(Constants.IMAGE)
        val mobile = stringPreferencesKey(Constants.MOBILE)
    }

    val firstName = preferences.data.map {
        it[Keys.firstName]
    }

    val lastName = preferences.data.map {
        it[Keys.lastName]
    }

    val token = preferences.data.map {
        it[Keys.token]
    }


    val image = preferences.data.map {
        it[Keys.image]
    }

    val mobile = preferences.data.map {
        it[Keys.mobile]
    }

    init {
        initializeApp()
    }

    private fun initializeApp() = scope.launch(Dispatchers.IO) {
        launch {
            token.collectLatest {
                CurrentUser.setToken(it)
            }
        }
        launch {
            mobile.collectLatest {
                CurrentUser.setMobile(it)
            }
        }
    }

    suspend fun setImage(value: String) {
        preferences.edit {
            it[Keys.image] = value
        }
    }

    suspend fun setToken(value: String) {
        CurrentUser.setToken(value)
        preferences.edit {
            it[Keys.token] = value
        }
    }

    suspend fun setMobile(value: String) {
        CurrentUser.setMobile(value)
        preferences.edit {
            it[Keys.mobile] = value
        }
    }

    suspend fun setFirstName(value: String) {
        preferences.edit {
            it[Keys.firstName] = value
        }
    }

    suspend fun setLastName(value: String) {
        preferences.edit {
            it[Keys.lastName] = value
        }
    }
}