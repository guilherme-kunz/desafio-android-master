package com.picpay.desafio.android

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUsers_retrievesSameUsers() {
        val user = User(id = 1001, img = "https://randomuser.me/api/portraits/men/9.jpg", name = "Eduardo Santos", username = "@eduardo.santos")
        userDao.insertUsers(listOf(user))

        val result = userDao.getAllUsers()

        Assert.assertEquals(result[0], user)
    }
}