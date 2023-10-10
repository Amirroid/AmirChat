package ir.amirroid.amirchat.data.repositories.users

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import ir.amirroid.amirchat.data.helpers.MediaHelper
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.getName
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class UsersRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val mediaHelper: MediaHelper
) {
    private val users = firestore.collection(Constants.USERS)

    fun queryWithId(id: String, onComplete: (List<UserModel>) -> Unit) {
        users.get().addOnCompleteListener {
            it.result.toObjects(UserModel::class.java).apply {
                val newData = filter { user ->
                    user.token != CurrentUser.token && (user.userId.contains(
                        id,
                        true
                    ) || user.getName()
                        .contains(id, true))
                }
                newData.apply(onComplete)
            }
        }
    }

    fun getUserWithId(id: String, onResponse: (UserModel?) -> Unit) {
        users.whereEqualTo("userId", id).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.firstOrNull()?.toObject(UserModel::class.java)
                onResponse.invoke(user)
            } else {
                onResponse.invoke(null)
            }
        }
    }

    fun getAllUsersFromContacts(
        scope: CoroutineScope,
        onResponse: (List<UserModel>) -> Unit
    ) {
        mediaHelper.getContacts(scope) {
            val listNumbers = mutableListOf<String>()
            it.map { contact -> contact.numbers }.forEach { numbers ->
                numbers.forEach { number -> listNumbers.add(number) }
            }
            users.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val users = task.result.toObjects(UserModel::class.java)
                            .map { model -> model ?: UserModel() }
                            .filter { user -> listNumbers.contains(user.mobileNumber) }
                        users.apply(onResponse)
                    } else {
                        onResponse.invoke(emptyList())
                    }
                }
        }
    }
}