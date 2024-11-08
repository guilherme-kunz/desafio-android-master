package com.picpay.desafio.android

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context: Context = mock()
    private val connectivityManager: ConnectivityManager = mock()
    private val networkCapabilities: NetworkCapabilities = mock()
    private val userDao: UserDao = mock()
    private val service: PicPayService = mock()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())

        val mockApplication: Application = mock()
        whenever(mockApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        whenever(connectivityManager.activeNetwork).thenReturn(mock())
        whenever(connectivityManager.getNetworkCapabilities(any())).thenReturn(networkCapabilities)

        viewModel = MainViewModel(connectivityManager, userDao, service)
    }

    @Test
    fun fetchUsers_withInternetConnection_loadsUsersFromApi() = runBlockingTest {
        val expectedUsers = listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))
        val call = mock<Call<List<User>>>()
        whenever(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(call.execute()).thenReturn(Response.success(expectedUsers))
        whenever(service.getUsers()).thenReturn(call)

        val observer: Observer<List<User>> = mock()
        viewModel.users.observeForever(observer)

        viewModel.fetchUsers()

        verify(observer).onChanged(expectedUsers)
        verify(userDao).insertUsers(expectedUsers)
    }

    @Test
    fun fetchUsers_withoutInternetConnection_loadsUsersFromCache() = runBlockingTest {
        val cachedUsers = listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))
        whenever(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)
        whenever(userDao.getAllUsers()).thenReturn(cachedUsers)

        val observer: Observer<List<User>> = mock()
        viewModel.users.observeForever(observer)

        viewModel.fetchUsers()

        verify(observer).onChanged(cachedUsers)
    }

    @Test
    fun fetchUsers_withoutInternetConnectionAndEmptyCache_showsErrorMessage() = runBlockingTest {
        whenever(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)
        whenever(userDao.getAllUsers()).thenReturn(emptyList())

        val observer: Observer<String?> = mock()
        viewModel.errorMessage.observeForever(observer)

        viewModel.fetchUsers()

        verify(observer).onChanged("No internet and no cached data")
    }

    @Test
    fun fetchUsers_withApiError_showsErrorMessage() = runBlockingTest {
        val call = mock<Call<List<User>>>()
        whenever(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(call.execute()).thenReturn(Response.error(500, mock()))
        whenever(service.getUsers()).thenReturn(call)

        val observer: Observer<String?> = mock()
        viewModel.errorMessage.observeForever(observer)

        viewModel.fetchUsers()

        verify(observer).onChanged("Failed to load data")
    }

    @Test
    fun fetchUsers_withException_showsErrorMessage() = runBlockingTest {
        whenever(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(service.getUsers()).thenThrow(RuntimeException("Network error"))

        val observer: Observer<String?> = mock()
        viewModel.errorMessage.observeForever(observer)

        viewModel.fetchUsers()

        verify(observer).onChanged("An error occurred: Network error")
    }
}