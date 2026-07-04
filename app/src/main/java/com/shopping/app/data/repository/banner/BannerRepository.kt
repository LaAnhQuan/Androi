package com.shopping.app.data.repository.banner

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

interface BannerRepository {

    fun getBanners(): Task<QuerySnapshot>

}
