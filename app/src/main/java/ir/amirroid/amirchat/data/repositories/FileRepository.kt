package ir.amirroid.amirchat.data.repositories

import ir.amirroid.amirchat.data.helpers.MediaHelper
import ir.amirroid.amirchat.data.models.media.ContactModel
import ir.amirroid.amirchat.data.models.media.FileModel
import ir.amirroid.amirchat.data.models.media.MediaModel
import ir.amirroid.amirchat.data.models.media.MusicModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val mediaHelper: MediaHelper
) {
    fun getMedias(
        scope: CoroutineScope,
        onReceive: (List<MediaModel>) -> Unit
    ) = mediaHelper.getMedias(scope, onReceive)

    fun getFile(
        scope: CoroutineScope,
        onReceive: (List<FileModel>) -> Unit
    ) = mediaHelper.getAllFiles(scope, onReceive)

    fun getContacts(
        scope: CoroutineScope,
        onReceive: (List<ContactModel>) -> Unit
    ) = mediaHelper.getContacts(scope, onReceive)

    fun getMusics(
        scope: CoroutineScope,
        onReceive: (List<MusicModel>) -> Unit
    ) = mediaHelper.getMusics(scope, onReceive)
}