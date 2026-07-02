package com.shopping.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.User
import com.shopping.app.data.repository.user.UserRepositoryImpl
import com.shopping.app.databinding.FragmentAdminBinding
import com.shopping.app.ui.admin.adapter.UserAdapter
import com.shopping.app.ui.admin.viewmodel.AdminViewModel
import com.shopping.app.ui.admin.viewmodel.AdminViewModelFactory
import com.shopping.app.ui.loadingprogress.LoadingProgressBar
import com.shopping.app.utils.Constants

class AdminFragment : Fragment() {

    private lateinit var bnd: FragmentAdminBinding
    private lateinit var loadingProgressBar: LoadingProgressBar
    private val viewModel by viewModels<AdminViewModel> {
        AdminViewModelFactory(UserRepositoryImpl())
    }

    private val roles = arrayOf(Constants.ROLE_CUSTOMER, Constants.ROLE_SELLER, Constants.ROLE_ADMIN)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_admin, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = LoadingProgressBar(requireContext())
        bnd.rvUsers.layoutManager = LinearLayoutManager(requireContext())

        viewModel.usersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> loadingProgressBar.show()
                is DataState.Success -> {
                    loadingProgressBar.hide()
                    it.data?.let { users ->
                        bnd.rvUsers.adapter = UserAdapter(users) { user -> showRoleDialog(user) }
                    }
                }
                is DataState.Error -> {
                    loadingProgressBar.hide()
                    Snackbar.make(bnd.root, it.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.updateRoleLiveData.observe(viewLifecycleOwner) {
            if (it is DataState.Success) {
                Snackbar.make(bnd.root, getString(R.string.role_updated), Snackbar.LENGTH_SHORT).show()
            } else if (it is DataState.Error) {
                Snackbar.make(bnd.root, it.message, Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun showRoleDialog(user: User) {

        // Options: 3 roles + delete
        val options = roles + getString(R.string.delete_user)

        AlertDialog.Builder(requireContext())
            .setTitle(user.username)
            .setItems(options) { dialog, which ->
                dialog.dismiss()
                if (which < roles.size) {
                    user.uid?.let { uid -> viewModel.updateRole(uid, roles[which]) }
                } else {
                    confirmDelete(user)
                }
            }
            .show()

    }

    private fun confirmDelete(user: User) {

        // Prevent an admin from deleting their own account
        if (user.uid == FirebaseAuth.getInstance().uid) {
            Snackbar.make(bnd.root, getString(R.string.cannot_delete_self), Snackbar.LENGTH_LONG).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle(user.username)
            .setMessage(getString(R.string.delete_user_confirm))
            .setPositiveButton(getString(R.string.delete)) { d, _ ->
                d.dismiss()
                user.uid?.let { uid -> viewModel.deleteUser(uid) }
            }
            .setNegativeButton(getString(R.string.cancel)) { d, _ -> d.dismiss() }
            .show()

    }

}
