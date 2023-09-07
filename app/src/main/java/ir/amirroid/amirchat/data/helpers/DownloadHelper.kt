package ir.amirroid.amirchat.data.helpers

import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import javax.inject.Inject

class DownloadHelper @Inject constructor() {
    private val cancelList = mutableListOf<String>()

    fun addCancel(url: String) {
        cancelList.add(url)
    }

    fun downloadWithProgress(link: String, onResponse: (DownloadData) -> Unit) {
        getExecutor {
            var ins: InputStream? = null
            var fos: FileOutputStream? = null
            var httpUrl: HttpURLConnection? = null
            try {
                val url = URL(link)
                httpUrl = url.openConnection() as HttpURLConnection
                httpUrl.connect()
                if (httpUrl.responseCode != HttpURLConnection.HTTP_OK) {
                    onResponse.invoke(
                        DownloadData(
                            link,
                            -1,
                            DownloadState.ERROR
                        )
                    )
                    return@getExecutor
                }
                val length = httpUrl.contentLength
                ins = httpUrl.inputStream
                fos = FileOutputStream(Environment.DIRECTORY_DOWNLOADS)
                val bytes = ByteArray(1024)
                var count = 0
                var total = 0
                while (ins.read(bytes).also { count = it } != -1) {
                    if (cancelList.contains(link)) {
                        cancelList.remove(link)
                        ins.close()
                        return@getExecutor
                    }
                    total += count
                    fos.write(bytes, 0, count)
                    onResponse.invoke(
                        DownloadData(
                            link,
                            total * 100 / length,
                            DownloadState.IN_PROGRESS
                        )
                    )

                }
            } catch (e: Exception) {
                Log.e("DOWNLOAD", "downloadWithProgress: ${e.localizedMessage}")
            } finally {
                onResponse.invoke(
                    DownloadData(
                        link,
                        100,
                        DownloadState.SUCCESS
                    )
                )
                ins?.close()
                fos?.close()
                fos?.flush()
                httpUrl?.disconnect()
            }
        }
    }

    private fun getExecutor(block: () -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            handler.post(block)
        }
    }
}

data class DownloadData(
    val url: String,
    val progress: Int,
    val state: DownloadState
)

enum class DownloadState {
    ERROR,
    SUCCESS,
    IN_PROGRESS
}