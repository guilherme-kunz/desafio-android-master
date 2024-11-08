package com.picpay.desafio.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.picpay.desafio.android.model.User

@Dao
interface UsersDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<User>)
}