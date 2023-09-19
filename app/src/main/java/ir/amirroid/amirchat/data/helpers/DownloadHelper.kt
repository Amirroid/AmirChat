package ir.amirroid.amirchat.data.helpers

import android.os.Environment
import android.util.Log
import com.google.firebase.storage.StorageReference
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.divIfNotZero
import java.io.File
import javax.inject.Inject

class DownloadHelper @Inject constructor(
    private val reference: StorageReference
) {

    private val storageReference = reference.child(Constants.CHATS)
    private val cancelList = mutableListOf<String>()


    fun addCancel(url: String) {
        cancelList.add(url)
    }

    fun downloadWithProgress(link: String, onResponse: (FileNetData) -> Unit) {
        val downloadFile =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadFile, link)
        if (file.exists().not()) file.createNewFile()
        storageReference.child(link).getFile(file)
            .addOnSuccessListener {
                onResponse.invoke(
                    FileNetData(
                        link,
                        1f,
                        DownloadState.SUCCESS
                    )
                )
            }.addOnFailureListener {
                Log.d("ewfeuf", "downloadWithProgress: ${it.message}")
                onResponse.invoke(
                    FileNetData(
                        link,
                        -1f,
                        DownloadState.ERROR
                    )
                )
            }.addOnProgressListener {
                val progress =
                    it.bytesTransferred.toFloat().divIfNotZero(it.totalByteCount.toFloat())
                Log.d("ewfeuf", "downloadWithProgress: $progress")
                onResponse.invoke(
                    FileNetData(
                        link,
                        progress,
                        DownloadState.IN_PROGRESS
                    )
                )
            }
    }
}

data class FileNetData(
    val url: String,
    val progress: Float,
    val state: DownloadState
)

enum class DownloadState {
    ERROR,
    SUCCESS,
    IN_PROGRESS
}