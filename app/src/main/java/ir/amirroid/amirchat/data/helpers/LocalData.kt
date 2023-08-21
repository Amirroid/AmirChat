package ir.amirroid.amirchat.data.helpers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.preferences
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalData @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val dataStore = context.preferences

    object Keys {
        val rooms = stringPreferencesKey(Constants.ROOMS)
    }

    val rooms = dataStore.data.map {
        it[Keys.rooms]
    }


    suspend fun setRooms(room: String) {
        dataStore.edit {
            it[Keys.rooms] = room
        }
    }
}