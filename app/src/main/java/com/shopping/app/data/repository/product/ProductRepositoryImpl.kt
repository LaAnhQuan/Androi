package com.shopping.app.data.repository.product

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.data.model.Product
import com.shopping.app.utils.Constants

class ProductRepositoryImpl : ProductRepository {

    private fun products() = Firebase.firestore.collection(Constants.DATABASE_PRODUCTS_TABLE)

    override fun getProducts(): Task<QuerySnapshot> {
        return products().get()
    }

    override fun addProduct(product: Product): Task<Void> {
        val doc = products().document() // auto-generated id
        product.id = doc.id
        product.sellerId = FirebaseAuth.getInstance().uid
        return doc.set(product)
    }

    override fun getProductsBySeller(uid: String): Task<QuerySnapshot> {
        return products().whereEqualTo("sellerId", uid).get()
    }

    override fun updateProduct(product: Product): Task<Void> {
        return products().document(product.id!!).set(product)
    }

    override fun deleteProduct(productId: String): Task<Void> {
        return products().document(productId).delete()
    }

}
