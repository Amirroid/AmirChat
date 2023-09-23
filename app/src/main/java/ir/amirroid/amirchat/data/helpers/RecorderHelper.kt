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
        return createFileInAppDirectory(
            (System.currentTimeMillis().formatDateTimeForFile() + ".mp3").trim()
        ).path
    }

    private fun createFileInAppDirectory(fileName: String): File {
        val appDirectory = createAmirChatDirectory()
        val file = File(appDirectory, fileName)
        Log.d("FILE_GENERATOR", "createFileInAppDirectory: $file")
        if (file.exists().not()) file.createNewFile()
        return file
    }

    private fun createAmirChatDirectory(): File {
        val amirChatDirectory = File(Environment.getExternalStorageDirectory(), "AmirChat")
        Log.d("dsfdsds", "createAmirChatDirectory: ${amirChatDirectory.path}")

        if (amirChatDirectory.exists().not()) amirChatDirectory.mkdir()

        val musicDirectory = File(amirChatDirectory, "Musics")

        if (musicDirectory.exists().not()) musicDirectory.mkdir()

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

    fun stop(onInfo: () -> Unit) {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.setOnInfoListener { _, _, _ ->
                onInfo.invoke()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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