package ir.amirroid.amirchat.viewmodels.qr_code

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.helpers.QrCodeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QrCodeProfileViewModel @Inject constructor(
    private val qrCodeHelper: QrCodeHelper
) : ViewModel() {
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    fun generateQrCode(text: String) = viewModelScope.launch(Dispatchers.Default) {
        _bitmap.value = qrCodeHelper.generateQrCode(text)
    }
}