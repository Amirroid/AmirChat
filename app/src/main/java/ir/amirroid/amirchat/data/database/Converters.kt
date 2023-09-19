package ir.amirroid.amirchat.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import ir.amirroid.amirchat.data.models.chat.FileMessage

class Converters {
    @TypeConverter
    fun fileMessagesToJson(files:List<FileMessage>) : String{
        return Gson().toJson(files)
    }
    @TypeConverter
    fun jsonToFileMessages(json:String) : List<FileMessage>{
        return Gson().fromJson(json, Array<FileMessage>::class.java).toList()
    }
}