package ir.amirroid.amirchat.data.helpers

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.utils.formatDateTime
import ir.amirroid.amirchat.utils.formatDateTimeForFile
import ir.amirroid.amirchat.utils.formatTime
import java.io.File
import java.io.IOException
import javax.inject.Inject


class RecorderHelper @Inject constructor(
    @ApplicationContext val context: Context,
) {
    private var mediaRecorder: MediaRecorder? = null
    private fun createMediaRecorder(path: String) =
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context.applicationContext)
        } else {
            MediaRecorder()
        }).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(path)
        }

    fun generatePath(): String {
        return createFileInAppDirectory(context, System.currentTimeMillis().formatDateTimeForFile() + ".mp3")?.path
            ?: ""
    }

    private fun createFileInAppDirectory(context: Context, fileName: String): File? {
        val appDirectory = createCatchDirectory(context)
        if (appDirectory != null) {
            val file = File(appDirectory, fileName)
            Log.d("FILE_GENERATOR", "createFileInAppDirectory: $file")
            if (!file.exists()) file.createNewFile()
            return file
        }
        return null
    }

    private fun createCatchDirectory(context: Context): File? {
        val catchDirectory = context.getExternalFilesDir(null)
        val musicDirectory = File(catchDirectory, "Musics")

        if (!musicDirectory.exists()) {
            val directoryCreated = musicDirectory.mkdir()
            if (!directoryCreated) {
                // Failed to create the directory
                return null
            }
        }

        return musicDirectory
    }

    fun startRecording(path: String): Boolean {
        return try {
            createMediaRecorder(path).apply {
                mediaRecorder = this
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun stop() {
        try {
            mediaRecorder?.stop()
        }catch (e:Exception){e.printStackTrace()}
    }

    fun release() {
        mediaRecorder?.apply {
            stop()
            release()
        }
    }

    fun cancel(path: String) {
        try {
            release()
            mediaRecorder = null
            File(path).delete()
        } catch (_: Exception) {
        }
    }
}