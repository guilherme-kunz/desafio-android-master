package com.picpay.desafio.android.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.model.User
import com.picpay.desafio.android.repository.Repository
import com.picpay.desafio.android.service.NetworkResponse
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _users = MutableLiveData<List<User>?>()
    val users: MutableLiveData<List<User>?> get() = _users

    private val _error = MutableLiveData<Unit>()
    val error: LiveData<Unit> = _error

     fun verifyNetwork(context: Context) {
        if (isNetworkAvailable(context)) {
            fetchUsers()
        } else {
            getUsersFromDatabase()
        }
    }

    fun fetchUsers() {
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
                _error.value = Unit
            }
        }
        _loading.value = false
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}