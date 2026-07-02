package com.shopping.app.data.repository.user

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.data.model.User
import com.shopping.app.utils.Constants
import com.shopping.app.utils.Constants.DATABASE_FIELD_ROLE
import com.shopping.app.utils.Constants.DATABASE_FIELD_UID
import com.shopping.app.utils.Constants.DATABASE_FIELD_USERNAME
import com.shopping.app.utils.Constants.ROLE_CUSTOMER

class UserRepositoryImpl : UserRepository {

    override fun getUserData(user: User): DocumentReference {

        return Firebase.firestore.collection(Constants.DATABASE_USERS_TABLE)
            .document(user.uid!!)

    }

    override fun userAddDatabase(user: User): Task<Void> {

        val userMap = hashMapOf(
            DATABASE_FIELD_USERNAME to user.username,
            DATABASE_FIELD_UID to user.uid,
            DATABASE_FIELD_ROLE to (user.role ?: ROLE_CUSTOMER),
        )

        return Firebase.firestore.collection(Constants.DATABASE_USERS_TABLE)
            .document(user.uid.toString())
            .set(userMap)

    }

    override fun getAllUsers(): Task<QuerySnapshot> {
        return Firebase.firestore.collection(Constants.DATABASE_USERS_TABLE).get()
    }

    override fun updateUserRole(uid: String, role: String): Task<Void> {
        return Firebase.firestore.collection(Constants.DATABASE_USERS_TABLE)
            .document(uid)
            .update(DATABASE_FIELD_ROLE, role)
    }

    override fun deleteUser(uid: String): Task<Void> {
        return Firebase.firestore.collection(Constants.DATABASE_USERS_TABLE)
            .document(uid)
            .delete()
    }

}