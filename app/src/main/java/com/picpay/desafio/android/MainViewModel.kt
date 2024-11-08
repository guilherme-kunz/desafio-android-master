package com.picpay.desafio.android

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val connectivityManager: ConnectivityManager,
    private val userDao: UserDao,
    private val service: PicPayService
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchUsers() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isNetworkAvailable()) {
                    // Se houver conexão com a internet, tenta chamar a API
                    val response = service.getUsers().execute()
                    if (response.isSuccessful) {
                        response.body()?.let { users ->
                            // Persiste os dados no banco de dados para o cache
                            userDao.insertUsers(users)
                            _users.postValue(users)
                        }
                    } else {
                        _errorMessage.postValue("Failed to load data")
                    }
                } else {
                    // Se não houver conexão com a internet, tenta carregar do banco de dados (cache)
                    val cachedUsers = userDao.getAllUsers()
                    if (cachedUsers.isNotEmpty()) {
                        _users.postValue(cachedUsers)
                    } else {
                        _errorMessage.postValue("No internet and no cached data")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.postValue("An error occurred: ${e.localizedMessage}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}