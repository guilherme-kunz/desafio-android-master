package com.picpay.desafio.android

import com.picpay.desafio.android.database.dao.UsersDao
import com.picpay.desafio.android.model.User
import com.picpay.desafio.android.repository.RepositoryImpl
import com.picpay.desafio.android.service.PicPayService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import retrofit2.Response

class RepositoryImplTest {
    private val apiService: PicPayService = mockk()

    private val userDao: UsersDao = mockk()
    private val repositoryImpl = RepositoryImpl(apiService, userDao)

    @Test
    fun `when getUsers is called then it should call service getUsers`() {
        val user = User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos")
        coEvery { apiService.getUsers() } returns Response.success(listOf(user))
        runBlockingTest {
            repositoryImpl.getUsers()
        }

    }

    @Test
    fun `when getUsersFromDb is called then it should call users from db`() {
        val user = User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos")
        coEvery { userDao.getAll() } returns listOf(user)
        runBlockingTest {
            RepositoryImpl(apiService, userDao).getUsersFromDatabase()
        }
        coVerify { userDao.getAll() }
    }

    @Test
    fun `when insertUser is called then it should call insert in db`() {
        val user = User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos")
        runBlockingTest {
            userDao.insert(listOf(user))
        }
        coVerify { userDao.insert(listOf(user)) }

    }
}