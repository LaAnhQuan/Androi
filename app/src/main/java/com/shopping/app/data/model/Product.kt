package com.shopping.app.data.model

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("category")
    var category: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("price")
    var price: Double? = null,
    @SerializedName("rating")
    var rating: Rating? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("sellerId")
    var sellerId: String? = null,
    @SerializedName("stock")
    var stock: Int? = null
): Parcelable {

    // json convert method
    fun toJson(): String {
        return Gson().toJson(this)
    }

    // static json object
    companion object {
        fun fromJson(jsonValue: String): Product {
            return Gson().fromJson(jsonValue, Product::class.java)
        }
    }

}
