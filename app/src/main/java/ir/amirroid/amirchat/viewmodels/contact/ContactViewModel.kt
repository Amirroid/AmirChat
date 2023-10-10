package ir.amirroid.amirchat.viewmodels.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.data.repositories.users.UsersRepository
import ir.amirroid.amirchat.utils.getName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {
    private var mainContacts = emptyList<UserModel>()

    private val _contacts = MutableStateFlow<List<UserModel>>(emptyList())
    val contacts = _contacts.asStateFlow()

    var loading by mutableStateOf(true)

    var searchText by mutableStateOf("")

    init {
        usersRepository.getAllUsersFromContacts(viewModelScope) {
            mainContacts = it
            _contacts.value = it
            loading = false
        }
    }

    fun search(text: String) {
        searchText = text
        _contacts.value = if (text.isEmpty()) {
            mainContacts
        } else {
            mainContacts.filter { it.getName().contains(text, true) }
        }
    }
}