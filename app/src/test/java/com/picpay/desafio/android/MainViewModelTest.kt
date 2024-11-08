package com.picpay.desafio.android

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
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

    private lateinit var mainViewModel: MainViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mainViewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getUsersFromDatabase should load users from database if available`() = runTest {
        val userList = listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))
        `when`(repository.getUsersFromDatabase()).thenReturn(userList)
        val observer = mock(Observer::class.java) as Observer<List<User>?>
        mainViewModel.users.observeForever(observer)
        mainViewModel.getUsersFromDatabase()
        verify(repository, times(1)).getUsersFromDatabase()
        verify(observer).onChanged(userList)
        Assert.assertEquals(mainViewModel.users.value, userList)
        mainViewModel.users.removeObserver(observer)
    }

    @Test
    fun `getUsersFromDatabase should fetch users from network if database is empty`() = runTest {
        `when`(repository.getUsersFromDatabase()).thenReturn(emptyList())
        `when`(repository.getUsers()).thenReturn(NetworkResponse.Success(listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))))
        val observer = mock(Observer::class.java) as Observer<List<User>?>
        mainViewModel.users.observeForever(observer)
        mainViewModel.getUsersFromDatabase()
        verify(repository, times(1)).getUsersFromDatabase()
        verify(repository, times(1)).getUsers()
        mainViewModel.users.removeObserver(observer)
    }

    @Test
    fun `fetchUsers should set error when NetworkResponse is Error`() = runTest {
        `when`(repository.getUsers()).thenReturn(NetworkResponse.Error(Exception("Network error")))
        val errorObserver = mock(Observer::class.java) as Observer<Unit>
        mainViewModel.error.observeForever(errorObserver)
        mainViewModel.getUsersFromDatabase()
        verify(repository, times(1)).getUsers()
        verify(errorObserver).onChanged(Unit)
        Assert.assertNotNull(mainViewModel.error.value)

        mainViewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `fetchUsers should insert users to database when NetworkResponse is Success`() = runTest {
        val userList = listOf(User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos"))
        `when`(repository.getUsers()).thenReturn(NetworkResponse.Success(userList))
        mainViewModel.getUsersFromDatabase()
        verify(repository, times(1)).insertUsersFromDatabase(userList)
    }
}
