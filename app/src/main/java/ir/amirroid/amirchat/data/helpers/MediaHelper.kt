package ir.amirroid.amirchat.data.helpers

import android.content.ContentUris
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Contacts
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.amirchat.data.models.media.ContactModel
import ir.amirroid.amirchat.data.models.media.FileModel
import ir.amirroid.amirchat.data.models.media.MediaModel
import ir.amirroid.amirchat.data.models.media.MusicModel
import ir.amirroid.amirchat.utils.getType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MediaHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getMedias(scope: CoroutineScope, onReceive: (List<MediaModel>) -> Unit) {
        scope.launch(Dispatchers.IO) {
            val videos = async { getVideos() }
            val images = async { getImages() }
            val medias = images.await().toMutableList().apply { addAll(videos.await()) }
            onReceive.invoke(medias.sortedByDescending { it.dateAdded })
        }
    }

    private fun getVideos(): List<MediaModel> {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val dataP = MediaStore.Video.VideoColumns.DATA
        val idP = MediaStore.Video.VideoColumns._ID
        val nameP = MediaStore.Video.VideoColumns.DISPLAY_NAME
        val durationP = MediaStore.Video.VideoColumns.DURATION
        val dateAddedP = MediaStore.Video.VideoColumns.DATE_MODIFIED
        val projection = arrayOf(
            idP,
            nameP,
            durationP,
            dataP,
            dateAddedP
        )
        val sort = "${MediaStore.Video.VideoColumns.DATE_MODIFIED} DESC"
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sort
        )
        val list = mutableListOf<MediaModel>()
        cursor?.use {
            it.moveToFirst()
            val idC = it.getColumnIndex(idP)
            val nameC = it.getColumnIndex(nameP)
            val dataC = it.getColumnIndex(dataP)
            val durationC = it.getColumnIndex(durationP)
            val dateAddedC = it.getColumnIndex(dateAddedP)
            do {
                val id = it.getLong(idC)
                val name = it.getString(nameC)
                val duration = it.getLong(durationC)
                val data = it.getString(dataC)
                val dateAdded = it.getLong(dateAddedC)
                val uriVideo = ContentUris.withAppendedId(
                    uri,
                    id
                )
                list.add(
                    MediaModel(name, data, duration, id, uriVideo, dateAdded)
                )
            } while (it.moveToNext())
        }
        return list
    }

    private fun getImages(): List<MediaModel> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val dataP = MediaStore.Images.ImageColumns.DATA
        val idP = MediaStore.Images.ImageColumns._ID
        val nameP = MediaStore.Images.ImageColumns.DISPLAY_NAME
        val dateAddedP = MediaStore.Images.ImageColumns.DATE_MODIFIED
        val projection = arrayOf(
            idP,
            nameP,
            dataP,
            dateAddedP
        )
        val sort = "${MediaStore.Images.ImageColumns.DATE_MODIFIED} DESC"
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sort
        )
        val list = mutableListOf<MediaModel>()
        cursor?.use {
            it.moveToFirst()
            val idC = it.getColumnIndex(idP)
            val nameC = it.getColumnIndex(nameP)
            val dataC = it.getColumnIndex(dataP)
            val dateAddedC = it.getColumnIndex(dateAddedP)
            do {
                val id = it.getLong(idC)
                val name = it.getString(nameC)
                val data = it.getString(dataC)
                val dateAdded = it.getLong(dateAddedC)
                val uriVideo = ContentUris.withAppendedId(
                    uri,
                    id
                )
                list.add(
                    MediaModel(name, data, 0L, id, uriVideo, dateAdded)
                )
            } while (it.moveToNext())
        }
        return list
    }


    fun getMusics(scope: CoroutineScope, onReceive: (List<MusicModel>) -> Unit) {
        scope.launch(Dispatchers.IO) {
            val data = getMusics()
            onReceive.invoke(data)
        }
    }

    private fun getMusics(): List<MusicModel> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val dataP = MediaStore.Audio.AudioColumns.DATA
        val idP = MediaStore.Audio.AudioColumns._ID
        val nameP = MediaStore.Audio.AudioColumns.DISPLAY_NAME
        val artistP = MediaStore.Audio.AudioColumns.ARTIST
        val durationP = MediaStore.Audio.AudioColumns.DURATION
        val projection = arrayOf(
            idP,
            nameP,
            dataP,
            artistP,
            durationP
        )
        val sort = "${MediaStore.Images.ImageColumns.DATE_MODIFIED} DESC"
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sort
        )
        val list = mutableListOf<MusicModel>()
        cursor?.use {
            it.moveToFirst()
            val idC = it.getColumnIndex(idP)
            val nameC = it.getColumnIndex(nameP)
            val dataC = it.getColumnIndex(dataP)
            val artistC = it.getColumnIndex(artistP)
            val durationC = it.getColumnIndex(durationP)
            do {
                val id = it.getLong(idC)
                val name = it.getString(nameC)
                val data = it.getString(dataC)
                val uriMusic = ContentUris.withAppendedId(
                    uri,
                    id
                )
                val duration = it.getLong(durationC)
                val artist = it.getString(artistC)
                list.add(
                    MusicModel(name, artist, data, duration, id, uriMusic)
                )
            } while (it.moveToNext())
        }
        return list
    }


    fun getAllFiles(scope: CoroutineScope, onReceive: (List<FileModel>) -> Unit) {
        scope.launch(Dispatchers.IO) {
            val data = getAllFiles()
            onReceive.invoke(data)
        }
    }

    private fun getAllFiles(): List<FileModel> {
        val uri = MediaStore.Files.getContentUri("external")
        val mimeTypeP = MediaStore.Files.FileColumns.MIME_TYPE
        val nameP = MediaStore.Files.FileColumns.DISPLAY_NAME
        val dataP = MediaStore.Files.FileColumns.DATA
        val sizeP = MediaStore.Files.FileColumns.SIZE
        val idP = MediaStore.Files.FileColumns._ID
        val mimTypeC = MediaStore.Files.FileColumns.MIME_TYPE
        val selection = "$mimTypeC IN('application/pdf') OR $mimTypeC LIKE 'application/vnd%'"
        val projection = arrayOf(
            mimeTypeP,
            nameP,
            dataP,
            sizeP,
            idP,
        )
        val sort = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            sort
        )
        val list = mutableListOf<FileModel>()
        cursor?.use {
            it.moveToFirst()
            val nameC = it.getColumnIndex(nameP)
            val mimeTypeC = it.getColumnIndex(mimeTypeP)
            val sizeC = it.getColumnIndex(sizeP)
            val dataC = it.getColumnIndex(dataP)
            val idC = it.getColumnIndex(idP)
            do {
                val data = it.getString(dataC)
                if (data.endsWith(File.separator)) continue
                val name = it.getString(nameC) ?: ""
                val id = it.getLong(idC)
                val size = it.getLong(sizeC)
                val mimeType = it.getString(mimeTypeC) ?: name.getType()
                list.add(
                    FileModel(name, data, mimeType, size, id)
                )
            } while (it.moveToNext())
        }
        return list
    }


    fun getContacts(scope: CoroutineScope, onReceive: (List<ContactModel>) -> Unit) {
        scope.launch(Dispatchers.IO) {
            val data = getContacts()
            onReceive.invoke(data)
        }
    }

    private fun getContacts(): List<ContactModel> {
        val uri = Contacts.CONTENT_URI
        val nameP = Contacts.DISPLAY_NAME
        val idP = Contacts._ID
        val hasNumber = Contacts.HAS_PHONE_NUMBER
        val phoneP = CommonDataKinds.Phone.NUMBER
        val selection = "$hasNumber =?"
        val selectionArgs = arrayOf("1")
        val projection = arrayOf(
            idP,
            nameP,
        )
        val order = "$nameP ASC"
        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            order
        )
        val list = mutableListOf<ContactModel>()
        cursor?.use {
            it.moveToFirst()
            val nameC = it.getColumnIndex(nameP)
            val idC = it.getColumnIndex(idP)
            do {
                val name = it.getString(nameC)
                val id = it.getLong(idC)
                val cursorPoneNumbers = context.contentResolver.query(
                    CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(phoneP),
                    CommonDataKinds.Phone.CONTACT_ID + " =?",
                    arrayOf(id.toString()),
                    null
                )
                val phoneNumbers = mutableListOf<String>()
                cursorPoneNumbers?.use { cp ->
                    val phoneC = cp.getColumnIndex(phoneP)
                    cp.moveToFirst()
                    do {
                        phoneNumbers.add(cp.getString(phoneC))
                    } while (cp.moveToNext())
                }
                list.add(
                    ContactModel(id, name, phoneNumbers)
                )
            } while (it.moveToNext())
        }
        return list
    }
}