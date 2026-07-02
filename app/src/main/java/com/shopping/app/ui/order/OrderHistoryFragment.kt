package com.shopping.app.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.repository.order.OrderRepositoryImpl
import com.shopping.app.databinding.FragmentOrderHistoryBinding
import com.shopping.app.ui.loadingprogress.LoadingProgressBar
import com.shopping.app.ui.order.adapter.OrderAdapter
import com.shopping.app.ui.order.viewmodel.OrderViewModel
import com.shopping.app.ui.order.viewmodel.OrderViewModelFactory

class OrderHistoryFragment : Fragment() {

    private lateinit var bnd: FragmentOrderHistoryBinding
    private lateinit var loadingProgressBar: LoadingProgressBar
    private val viewModel by viewModels<OrderViewModel> {
        OrderViewModelFactory(OrderRepositoryImpl())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_order_history, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = LoadingProgressBar(requireContext())
        bnd.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        bnd.ivOrderBack.setOnClickListener { findNavController().popBackStack() }

        viewModel.ordersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> loadingProgressBar.show()
                is DataState.Success -> {
                    loadingProgressBar.hide()
                    val orders = it.data ?: emptyList()
                    bnd.rvOrders.adapter = OrderAdapter(orders)
                    bnd.tvOrderEmpty.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
                }
                is DataState.Error -> {
                    loadingProgressBar.hide()
                    Snackbar.make(bnd.root, it.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

    }

}
