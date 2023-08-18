package ir.amirroid.amirchat.viewmodels.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.data.repositories.users.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {
    private val _users = MutableStateFlow<List<UserModel>>(emptyList())
    val users = _users.asStateFlow()

    var text by mutableStateOf("")
    var loading by mutableStateOf(false)

    fun search() {
        if (text.isNotEmpty()) {
            loading = true
            _users.value = emptyList()
            usersRepository.queryWithId(text) {
                _users.value = it
                loading = false
            }
        } else {
            _users.value = emptyList()
        }
    }
}