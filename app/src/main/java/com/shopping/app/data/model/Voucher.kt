package com.shopping.app.data.model

data class Voucher(
    var code: String? = null,
    var type: String? = "percent",   // "percent" | "amount"
    var value: Double? = 0.0,        // percent (e.g. 10) or fixed amount
    var minOrder: Double? = 0.0,     // minimum subtotal required
    var active: Boolean = true
)
