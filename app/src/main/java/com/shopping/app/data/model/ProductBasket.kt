package com.shopping.app.data.model

class ProductBasket(
    var id: String? = "",          // basket line id (product id + variant suffix)
    var title: String? = "",
    var image: String? = "",
    var price: Double? = 0.0,
    var piece: Int? = 0,
    var sellerId: String? = "",
    var color: String? = "",
    var size: String? = "",
    var productId: String? = ""    // real product id (for stock updates)
)
