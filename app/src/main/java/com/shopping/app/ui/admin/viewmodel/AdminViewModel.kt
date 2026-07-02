package com.shopping.app.ui.admin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.User
import com.shopping.app.data.repository.user.UserRepository

class AdminViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _usersLiveData = MutableLiveData<DataState<List<User>?>>()
    val usersLiveData: LiveData<DataState<List<User>?>>
        get() = _usersLiveData

    private val _updateRoleLiveData = MutableLiveData<DataState<Boolean>>()
    val updateRoleLiveData: LiveData<DataState<Boolean>>
        get() = _updateRoleLiveData

    init {
        getUsers()
    }

    fun getUsers() {

        _usersLiveData.postValue(DataState.Loading())
        userRepository.getAllUsers()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.toObjects(User::class.java)
                _usersLiveData.postValue(DataState.Success(users))
            }
            .addOnFailureListener { e ->
                _usersLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

    fun updateRole(uid: String, role: String) {

        _updateRoleLiveData.value = DataState.Loading()
        userRepository.updateUserRole(uid, role)
            .addOnSuccessListener {
                _updateRoleLiveData.value = DataState.Success(true)
                getUsers() // refresh list
            }
            .addOnFailureListener { e ->
                _updateRoleLiveData.value = DataState.Error(e.message ?: "Error")
            }

    }

    fun deleteUser(uid: String) {

        _updateRoleLiveData.value = DataState.Loading()
        userRepository.deleteUser(uid)
            .addOnSuccessListener {
                _updateRoleLiveData.value = DataState.Success(true)
                getUsers() // refresh list
            }
            .addOnFailureListener { e ->
                _updateRoleLiveData.value = DataState.Error(e.message ?: "Error")
            }

    }

}
