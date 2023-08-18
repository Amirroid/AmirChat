package ir.amirroid.amirchat.viewmodels.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.utils.Constants
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenHelper: TokenHelper
) : ViewModel() {
    val image = tokenHelper.image
    val mobile = tokenHelper.mobile
    val firstName = tokenHelper.firstName
    val lastName = tokenHelper.lastName
}