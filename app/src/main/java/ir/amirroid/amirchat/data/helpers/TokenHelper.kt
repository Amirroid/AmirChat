package ir.amirroid.amirchat.data.helpers

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random


class TokenHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val preferences = context.preferences
    private fun generateRandomString() = Random.nextLong().toString(36).substring(1)
    fun generateToken(): String {
        var token = ""
        while (token.length < 72) {
            token += generateRandomString()
        }
        return token
    }

    object Keys {
        val firstName = stringPreferencesKey(Constants.FIRST_NAME)
        val lastName = stringPreferencesKey(Constants.LAST_NAME)
        val token = stringPreferencesKey(Constants.TOKEN)
    }

    val firstName = preferences.data.map {
        it[Keys.firstName] ?: ""
    }

    val lastName = preferences.data.map {
        it[Keys.lastName] ?: ""
    }

    val token = preferences.data.map {
        it[Keys.token] ?: ""
    }

    suspend fun initializeApp() = withContext(Dispatchers.IO) {
        launch {
            firstName.collectLatest {
                CurrentUser.setFirstName(it)
            }
        }
        launch {
            lastName.collectLatest {
                CurrentUser.setLastName(it)
            }
        }
        launch {
            token.collectLatest {
                CurrentUser.setToken(it)
            }
        }
    }
}