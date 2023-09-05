package ir.amirroid.amirchat.data.repositories.users

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.getName
import javax.inject.Inject

class UsersRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val users = firestore.collection(Constants.USERS)

    fun queryWithId(id: String, onComplete: (List<UserModel>) -> Unit) {
        users.get().addOnCompleteListener {
            it.result.toObjects(UserModel::class.java).apply {
                val newData = filter { user ->
                    user.token != CurrentUser.token && user.userId.contains(id) || user.getName()
                        .contains(id)
                }
                newData.apply(onComplete)
            }
        }
    }
}