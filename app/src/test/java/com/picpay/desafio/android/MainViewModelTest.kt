package com.picpay.desafio.android

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.picpay.desafio.android.model.User
import com.picpay.desafio.android.repository.Repository
import com.picpay.desafio.android.service.NetworkResponse
import com.picpay.desafio.android.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var mainViewModel: MainViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mainViewModel = MainViewModel(repository)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verifyNetwork should fetch users if network is available`() = runTest {
        val networkCapabilities: NetworkCapabilities = mock(NetworkCapabilities::class.java)
        `when`(connectivityManager.activeNetwork).thenReturn(mock())
        `when`(connectivityManager.getNetworkCapabilities(any())).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        val userList = listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))
        `when`(repository.getUsers()).thenReturn(NetworkResponse.Success(userList))
        val observer = mock(Observer::class.java) as Observer<List<User>?>
        val loadingObserver = mock(Observer::class.java) as Observer<Boolean>
        mainViewModel.users.observeForever(observer)
        mainViewModel.loading.observeForever(loadingObserver)
        mainViewModel.verifyNetwork(context)
        verify(repository, times(1)).getUsers()
        verify(observer).onChanged(userList)
        verify(loadingObserver, times(1)).onChanged(true)
        verify(loadingObserver, times(1)).onChanged(false)
        mainViewModel.users.removeObserver(observer)
        mainViewModel.loading.removeObserver(loadingObserver)
    }

    @Test
    fun `verifyNetwork should get users from database if network is unavailable`() = runTest {
        `when`(connectivityManager.activeNetwork).thenReturn(null)
        val userList = listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))
        `when`(repository.getUsersFromDatabase()).thenReturn(userList)
        val observer = mock(Observer::class.java) as Observer<List<User>?>
        val loadingObserver = mock(Observer::class.java) as Observer<Boolean>
        mainViewModel.users.observeForever(observer)
        mainViewModel.loading.observeForever(loadingObserver)
        mainViewModel.verifyNetwork(context)
        verify(repository).getUsersFromDatabase()
        verify(observer).onChanged(userList)
        verify(loadingObserver, times(1)).onChanged(true)
        verify(loadingObserver, times(1)).onChanged(false)
        mainViewModel.users.removeObserver(observer)
        mainViewModel.loading.removeObserver(loadingObserver)
    }

    @Test
    fun `getUsersFromDatabase should set error when database is empty`() = runTest {
        `when`(repository.getUsersFromDatabase()).thenReturn(emptyList())
        val errorObserver = mock(Observer::class.java) as Observer<Unit>
        mainViewModel.error.observeForever(errorObserver)
        mainViewModel.getUsersFromDatabase()
        verify(repository).getUsersFromDatabase()
        verify(errorObserver).onChanged(Unit)
        mainViewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `fetchUsers should set error when NetworkResponse is Error`() = runTest {
        `when`(repository.getUsers()).thenReturn(NetworkResponse.Error(Exception("Network error")))
        val errorObserver = mock(Observer::class.java) as Observer<Unit>
        mainViewModel.error.observeForever(errorObserver)
        mainViewModel.fetchUsers()
        verify(repository).getUsers()
        verify(errorObserver).onChanged(Unit)
        mainViewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `fetchUsers should insert users to database when NetworkResponse is Success`() = runTest {
        val userList = listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))
        `when`(repository.getUsers()).thenReturn(NetworkResponse.Success(userList))
        mainViewModel.fetchUsers()
        verify(repository).insertUsersFromDatabase(userList)
    }
}