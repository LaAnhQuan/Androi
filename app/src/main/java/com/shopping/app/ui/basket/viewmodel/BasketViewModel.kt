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
import com.shopping.app.data.repository.basket.BasketRepository
import com.shopping.app.data.repository.notification.NotificationRepositoryImpl
import com.shopping.app.data.repository.order.OrderRepository
import com.shopping.app.data.repository.product.ProductRepositoryImpl
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


    init {
        _basketTotalLiveData.value = 0.0
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

                }else{
                    _basketLiveData.value = DataState.Error(error.message!!)
                }

            }

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

            val order = Order(
                buyerId = uid,
                items = basketList.toList(),
                total = _basketTotalLiveData.value ?: 0.0,
                createdAt = System.currentTimeMillis(),
                sellerIds = sellerIds
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
            val orderTotal = _basketTotalLiveData.value ?: 0.0
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

        _purchaseLiveData.value = DataState.Success(R.string.purchase_success_message)

    }

}