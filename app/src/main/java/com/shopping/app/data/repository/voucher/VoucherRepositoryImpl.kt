package com.shopping.app.data.repository.voucher

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.utils.Constants

class VoucherRepositoryImpl : VoucherRepository {

    private fun vouchers() = Firebase.firestore.collection(Constants.DATABASE_VOUCHERS_TABLE)

    override fun getVoucher(code: String): Task<DocumentSnapshot> {
        // voucher document id = code (uppercased)
        return vouchers().document(code.trim().uppercase()).get()
    }

    override fun getVouchers(): Task<QuerySnapshot> {
        return vouchers().whereEqualTo("active", true).get()
    }

}
