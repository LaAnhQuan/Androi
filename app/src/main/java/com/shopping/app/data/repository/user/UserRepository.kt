package com.shopping.app.data.repository.user

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.shopping.app.data.model.User

interface UserRepository {

    fun getUserData(user: User): DocumentReference

    fun userAddDatabase(user: User): Task<Void>

    fun getAllUsers(): Task<QuerySnapshot>

    fun updateUserRole(uid: String, role: String): Task<Void>

    fun deleteUser(uid: String): Task<Void>

}
