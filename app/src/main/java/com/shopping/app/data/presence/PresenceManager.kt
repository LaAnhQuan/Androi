package com.shopping.app.data.presence

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.utils.Constants

/**
 * Tracks user online presence in Firestore ("status" collection).
 * status/{uid} = { online: Boolean, lastSeen: Long (epoch millis) }
 */
object PresenceManager {

    private fun statusCollection() =
        Firebase.firestore.collection(Constants.DATABASE_STATUS_TABLE)

    fun statusRef(uid: String): DocumentReference = statusCollection().document(uid)

    fun setOnline() {
        val uid = FirebaseAuth.getInstance().uid ?: return
        statusRef(uid).set(
            mapOf(
                "online" to true,
                "lastSeen" to System.currentTimeMillis()
            )
        )
    }

    fun setOffline() {
        val uid = FirebaseAuth.getInstance().uid ?: return
        statusRef(uid).set(
            mapOf(
                "online" to false,
                "lastSeen" to System.currentTimeMillis()
            )
        )
    }

}
