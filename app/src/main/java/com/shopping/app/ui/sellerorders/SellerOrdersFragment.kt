package com.shopping.app.ui.sellerorders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Order
import com.shopping.app.data.repository.order.OrderRepositoryImpl
import com.shopping.app.utils.Constants
import com.shopping.app.databinding.FragmentSellerOrdersBinding
import com.shopping.app.ui.loadingprogress.LoadingProgressBar
import com.shopping.app.ui.sellerorders.adapter.SellerOrderAdapter
import com.shopping.app.ui.sellerorders.viewmodel.SellerOrdersViewModel
import com.shopping.app.ui.sellerorders.viewmodel.SellerOrdersViewModelFactory

class SellerOrdersFragment : Fragment() {

    private lateinit var bnd: FragmentSellerOrdersBinding
    private lateinit var loadingProgressBar: LoadingProgressBar
    private var myUid: String = ""
    private val viewModel by viewModels<SellerOrdersViewModel> {
        SellerOrdersViewModelFactory(OrderRepositoryImpl())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_seller_orders, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myUid = FirebaseAuth.getInstance().uid ?: ""
        loadingProgressBar = LoadingProgressBar(requireContext())
        bnd.rvSellerOrders.layoutManager = LinearLayoutManager(requireContext())
        bnd.ivSellerOrdersBack.setOnClickListener { findNavController().popBackStack() }

        setupObservers()
    }

    private val statuses = arrayOf(
        Constants.ORDER_STATUS_PENDING,
        Constants.ORDER_STATUS_SHIPPING,
        Constants.ORDER_STATUS_COMPLETED
    )

    private fun showStatusDialog(order: Order) {
        val labels = arrayOf(
            getString(R.string.status_pending),
            getString(R.string.status_shipping),
            getString(R.string.status_completed)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.update_status))
            .setItems(labels) { d, which ->
                d.dismiss()
                order.orderId?.let { viewModel.updateStatus(it, statuses[which]) }
            }
            .show()
    }

    private fun setupObservers() {

        viewModel.ordersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> loadingProgressBar.show()
                is DataState.Success -> {
                    loadingProgressBar.hide()
                    val orders = it.data ?: emptyList()
                    bnd.rvSellerOrders.adapter = SellerOrderAdapter(orders, myUid) { order ->
                        showStatusDialog(order)
                    }
                    bnd.tvSellerOrdersEmpty.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
                }
                is DataState.Error -> {
                    loadingProgressBar.hide()
                    Snackbar.make(bnd.root, it.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

    }

}
