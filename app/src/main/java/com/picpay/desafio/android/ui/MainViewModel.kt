package com.picpay.desafio.android.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.model.User
import com.picpay.desafio.android.model.UserDataSource
import com.picpay.desafio.android.repository.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository,
                    private val userDataSource: UserDataSource,
                    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val _loading = MutableStateFlow<Boolean>(true)
    val loading: StateFlow<Boolean> get() = _loading.asStateFlow()

    private val _users = MutableStateFlow<List<User>?>(null)
    val users: StateFlow<List<User>?> get() = _users.asStateFlow()

    private val _error = MutableStateFlow<Unit?>(null)
    val error: StateFlow<Unit?> = _error.asStateFlow()

     fun verifyNetwork(context: Context) {
        if (isNetworkAvailable(context)) {
            fetchUsers()
        } else {
            getUsersFromDatabase()
        }
    }

    fun fetchUsers() {
        viewModelScope.launch(ioDispatcher) {
            _loading.value = true
            try {
                val userList = userDataSource.getUsers()
                _users.value = userList
                repository.insertUsersFromDatabase(userList)
            } catch (e: Exception) {
                _error.value = Unit
            } finally {
                _loading.value = false
            }
        }
    }
    fun getUsersFromDatabase() {
        _loading.value = true
        viewModelScope.launch(ioDispatcher) {
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