package com.shopping.app.ui.basket.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Notification
import com.shopping.app.data.model.Order
import com.shopping.app.data.model.ProductBasket
import com.shopping.app.data.model.Voucher
import com.shopping.app.data.repository.basket.BasketRepository
import com.shopping.app.data.repository.notification.NotificationRepositoryImpl
import com.shopping.app.data.repository.order.OrderRepository
import com.shopping.app.data.repository.product.ProductRepositoryImpl
import com.shopping.app.data.repository.voucher.VoucherRepositoryImpl
import com.shopping.app.utils.Constants

class BasketViewModel(
    private val basketRepository: BasketRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private var _basketTotalLiveData = MutableLiveData<Double>()
    val basketTotalLiveData: LiveData<Double>
        get() = _basketTotalLiveData


    var basketList = mutableListOf<ProductBasket>()
    private var _basketLiveData = MutableLiveData<DataState<List<ProductBasket>?>>()
    val basketLiveData: LiveData<DataState<List<ProductBasket>?>>
        get() = _basketLiveData


    private var _updateProductPieceLiveData = MutableLiveData<DataState<Int>>()
    val updateProductPieceLiveData: LiveData<DataState<Int>>
        get() = _updateProductPieceLiveData


    private var _purchaseLiveData = MutableLiveData<DataState<Int>>()
    val purchaseLiveData: LiveData<DataState<Int>>
        get() = _purchaseLiveData


    // --- voucher / discount ---
    private val _discountLiveData = MutableLiveData<Double>()
    val discountLiveData: LiveData<Double> get() = _discountLiveData

    private val _finalTotalLiveData = MutableLiveData<Double>()
    val finalTotalLiveData: LiveData<Double> get() = _finalTotalLiveData

    private val _voucherMsgLiveData = MutableLiveData<String>()
    val voucherMsgLiveData: LiveData<String> get() = _voucherMsgLiveData

    private var appliedVoucher: Voucher? = null
    private var discount: Double = 0.0


    init {
        _basketTotalLiveData.value = 0.0
        _discountLiveData.value = 0.0
        _finalTotalLiveData.value = 0.0
        getProductsBasket()
    }

    private fun getProductsBasket(){

        basketRepository.getAllProductsBasket()
            .addSnapshotListener{ value, error ->

                if(error == null){

                    basketList = mutableListOf()
                    var total = 0.0

                    value?.forEach {
                        val product = it.toObject(ProductBasket::class.java)
                        total += product.price!! * product.piece!!
                        basketList.add(product)
                    }

                    _basketLiveData.value = DataState.Success(basketList)
                    _basketTotalLiveData.value = total
                    recomputeTotals(total)

                }else{
                    _basketLiveData.value = DataState.Error(error.message!!)
                }

            }

        }


    // --- voucher logic ---
    fun applyVoucher(code: String) {

        val subtotal = _basketTotalLiveData.value ?: 0.0

        if (code.isBlank()) {
            appliedVoucher = null
            recomputeTotals(subtotal)
            _voucherMsgLiveData.value = "Voucher removed"
            return
        }

        VoucherRepositoryImpl().getVoucher(code)
            .addOnSuccessListener { doc ->
                val voucher = if (doc.exists()) doc.toObject(Voucher::class.java) else null
                when {
                    voucher == null || !voucher.active ->
                        _voucherMsgLiveData.value = "Invalid voucher code"
                    subtotal < (voucher.minOrder ?: 0.0) ->
                        _voucherMsgLiveData.value = "Min order: %.2f$".format(voucher.minOrder ?: 0.0)
                    else -> {
                        appliedVoucher = voucher
                        recomputeTotals(subtotal)
                        _voucherMsgLiveData.value = "Voucher applied! -%.2f$".format(discount)
                    }
                }
            }
            .addOnFailureListener {
                _voucherMsgLiveData.value = "Could not apply voucher"
            }
    }

    private fun recomputeTotals(subtotal: Double) {
        val v = appliedVoucher
        if (v != null && subtotal < (v.minOrder ?: 0.0)) {
            appliedVoucher = null   // no longer eligible
        }
        discount = appliedVoucher?.let { computeDiscount(it, subtotal) } ?: 0.0
        _discountLiveData.value = discount
        _finalTotalLiveData.value = (subtotal - discount).coerceAtLeast(0.0)
    }

    private fun computeDiscount(v: Voucher, subtotal: Double): Double {
        val d = when (v.type) {
            "amount" -> v.value ?: 0.0
            else -> subtotal * (v.value ?: 0.0) / 100.0
        }
        return d.coerceAtMost(subtotal)
    }

    fun increaseProduct(productBasket: ProductBasket){

        if(productBasket.piece!! < 100){

            productBasket.piece = productBasket.piece!! + 1
            updateProductPiece(productBasket, true)

        }

    }


    fun reduceProduct(productBasket: ProductBasket){

        if(productBasket.piece!! > 1){

            productBasket.piece = productBasket.piece!! - 1
            updateProductPiece(productBasket, false)

        }else{
            deleteProduct(productBasket)
        }

    }

    private fun updateProductPiece(productBasket: ProductBasket, isIncrease: Boolean){

        basketRepository.updateProductsPiece(productBasket)
            .addOnSuccessListener {

                if(isIncrease) _updateProductPieceLiveData.value = DataState.Success(R.string.product_increased_message)
                else _updateProductPieceLiveData.value = DataState.Success(R.string.product_reduce_message)

            }
            .addOnFailureListener { e ->
                _updateProductPieceLiveData.value = DataState.Error(e.message!!)
            }

    }

    private fun deleteProduct(productBasket: ProductBasket){

        basketRepository.deleteProducts(productBasket)
            .addOnSuccessListener {
                _updateProductPieceLiveData.value = DataState.Success(R.string.product_deleted_message)
            }
            .addOnFailureListener { e ->
                _updateProductPieceLiveData.value = DataState.Error(e.message!!)
            }

    }

    fun clearTheBasket(){

        // save an order (history) before emptying the basket
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null && basketList.isNotEmpty()) {
            val sellerIds = basketList.mapNotNull { it.sellerId }
                .filter { it.isNotBlank() }
                .distinct()

            val finalTotal = _finalTotalLiveData.value ?: (_basketTotalLiveData.value ?: 0.0)

            val order = Order(
                buyerId = uid,
                items = basketList.toList(),
                total = finalTotal,
                createdAt = System.currentTimeMillis(),
                sellerIds = sellerIds,
                voucherCode = appliedVoucher?.code,
                discount = discount
            )
            orderRepository.addOrder(order)

            // decrease stock for each purchased product
            val productRepository = ProductRepositoryImpl()
            basketList.forEach { item ->
                val pid = item.productId
                val qty = item.piece ?: 0
                if (!pid.isNullOrBlank() && qty > 0) {
                    productRepository.decreaseStock(pid, qty)
                }
            }

            val notificationRepository = NotificationRepositoryImpl()

            // notify the BUYER that the order was placed successfully
            val orderTotal = finalTotal
            notificationRepository.addNotification(
                Notification(
                    userId = uid,
                    title = "Order placed",
                    body = "Your order was placed successfully. Total: %.2f$".format(orderTotal),
                    type = Constants.NOTIFICATION_TYPE_ORDER_PLACED
                )
            )

            // notify each seller that they got a new order
            for (sellerId in sellerIds) {
                notificationRepository.addNotification(
                    Notification(
                        userId = sellerId,
                        title = "New order",
                        body = "You have a new order for your shop.",
                        type = Constants.NOTIFICATION_TYPE_ORDER
                    )
                )
            }
        }

        basketList.forEach {
            deleteProduct(it)
        }

        // reset voucher after checkout
        appliedVoucher = null
        discount = 0.0

        _purchaseLiveData.value = DataState.Success(R.string.purchase_success_message)

    }

}