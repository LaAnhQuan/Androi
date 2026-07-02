package com.shopping.app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shopping.app.R
import com.shopping.app.data.model.Notification
import com.shopping.app.data.repository.notification.NotificationRepositoryImpl
import com.shopping.app.databinding.FragmentNotificationsBinding
import com.shopping.app.ui.notifications.adapter.NotificationAdapter
import com.shopping.app.ui.notifications.viewmodel.NotificationsViewModel
import com.shopping.app.ui.notifications.viewmodel.NotificationsViewModelFactory
import com.shopping.app.utils.Constants

class NotificationsFragment : Fragment() {

    private lateinit var bnd: FragmentNotificationsBinding
    private val viewModel by viewModels<NotificationsViewModel> {
        NotificationsViewModelFactory(NotificationRepositoryImpl())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bnd.rvNotifications.layoutManager = LinearLayoutManager(requireContext())

        viewModel.notificationsLiveData.observe(viewLifecycleOwner) { list ->
            bnd.rvNotifications.adapter = NotificationAdapter(list) { n -> onNotificationClick(n) }
            bnd.tvNotifEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

    }

    private fun onNotificationClick(n: Notification) {

        when (n.type) {
            // message -> open the conversation with the sender
            Constants.NOTIFICATION_TYPE_MESSAGE -> {
                val fromUid = n.fromUid
                if (!fromUid.isNullOrBlank()) {
                    findNavController().navigate(
                        R.id.action_notificationsFragment_to_chatFragment,
                        Bundle().apply {
                            putString(Constants.CHAT_OTHER_UID, fromUid)
                            putString(Constants.CHAT_OTHER_NAME, n.fromName ?: "")
                        }
                    )
                }
            }
            // seller new order -> open the seller's shop orders
            Constants.NOTIFICATION_TYPE_ORDER -> {
                findNavController().navigate(R.id.action_notificationsFragment_to_sellerOrdersFragment)
            }
            // buyer order placed -> open the buyer's order history
            Constants.NOTIFICATION_TYPE_ORDER_PLACED -> {
                findNavController().navigate(R.id.action_notificationsFragment_to_orderHistoryFragment)
            }
        }

    }

}
