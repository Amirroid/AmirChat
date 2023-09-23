package ir.amirroid.amirchat.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.amirroid.amirchat.data.models.chat.MessageModel

@Database(
    entities = [
        MessageModel::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sendingDao(): SendingMessagesDao
}