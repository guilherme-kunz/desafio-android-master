package com.picpay.desafio.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.model.User
import com.picpay.desafio.android.repository.Repository
import com.picpay.desafio.android.service.NetworkResponse
import kotlinx.coroutines.launch

class MainViewModel(val repository: Repository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _users = MutableLiveData<List<User>?>()
    val users: MutableLiveData<List<User>?> get() = _users

    private val _error = MutableLiveData<Unit>()
    val error: LiveData<Unit> = _error

    private fun fetchUsers() {
        _loading.value = true
        viewModelScope.launch {
            when (val response = repository.getUsers()) {
                is NetworkResponse.Error -> {
                    _error.value = Unit
                }
                is NetworkResponse.Success -> {
                    response.data.let {
                        _users.value = it
                        repository.insertUsersFromDatabase(it)
                    }
                }
            }
        }
        _loading.value = false
    }

    fun getUsersFromDatabase() {
        _loading.value = true
        viewModelScope.launch {
            val listOfUsers = repository.getUsersFromDatabase()
            if (!listOfUsers.isNullOrEmpty()) {
                _users.value = listOfUsers
            } else {
                fetchUsers()
            }
            _loading.value = false
        }
    }
}