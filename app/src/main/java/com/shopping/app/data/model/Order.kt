package com.shopping.app.data.model

data class Order(
    var orderId: String? = null,
    var buyerId: String? = null,
    var items: List<ProductBasket>? = null,
    var total: Double? = 0.0,
    var createdAt: Long? = 0L
)
