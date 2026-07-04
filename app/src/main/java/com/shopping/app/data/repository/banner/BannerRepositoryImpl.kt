package com.shopping.app.data.repository.banner

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.utils.Constants

class BannerRepositoryImpl : BannerRepository {

    override fun getBanners(): Task<QuerySnapshot> {
        return Firebase.firestore.collection(Constants.DATABASE_BANNERS_TABLE).get()
    }

}
