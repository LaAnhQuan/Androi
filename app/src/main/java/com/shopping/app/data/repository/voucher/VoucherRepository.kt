package com.shopping.app.data.repository.voucher

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

interface VoucherRepository {

    fun getVoucher(code: String): Task<DocumentSnapshot>

    fun getVouchers(): Task<QuerySnapshot>

}
