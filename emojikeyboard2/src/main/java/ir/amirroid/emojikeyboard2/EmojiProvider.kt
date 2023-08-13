package ir.amirroid.emojikeyboard2

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class EmojiProvider(private val context: Context) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job)

    private fun getJson(): String {
//        var bufferIns: BufferedReader? = null
//        var json = ""
//        try {
//            val inputStream = context.applicationContext.assets.open("emoji.json")
//            val insReader = InputStreamReader(inputStream)
//            bufferIns = BufferedReader(
//                insReader
//            )
//            var lineText = ""
//            while (bufferIns.readLine().also { lineText = it } != null) {
//                json += lineText
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            bufferIns?.close()
//        }
        return context.applicationContext.assets.open("emoji.json").readBytes().decodeToString()
    }

    fun jsonToData(
        group: String?,
        onProcess: (List<EmojiData>) -> Unit
    ) = scope.launch(Dispatchers.IO) {
        val json = getJson()
        val emojiListType = object : TypeToken<List<EmojiData>>() {}
        val listData: List<EmojiData> = (
                Gson().fromJson<List<EmojiData>>(json, emojiListType.type) ?: emptyList())

        if (group != null) {
            onProcess.invoke(listData.filter { it.group == group })
        } else
            onProcess.invoke(listData)
    }
}