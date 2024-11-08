package com.picpay.desafio.android.di

import android.app.Application
import androidx.room.Room
import com.picpay.desafio.android.database.AppDatabase
import com.picpay.desafio.android.database.dao.UsersDao
import com.picpay.desafio.android.repository.Repository
import com.picpay.desafio.android.repository.RepositoryImpl
import com.picpay.desafio.android.service.RetrofitBuilder
import com.picpay.desafio.android.ui.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

val repositoryModule = module {
    factory <Repository> {
        RepositoryImpl(
            api = RetrofitBuilder.getAllUsers(),
            usersDAO = get()
        )
    }
}

val userDateBuilder = module {
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application, AppDatabase::class.java, "user_app_db"
        )
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    }

    fun provideDao(database: AppDatabase): UsersDao {
        return database.usersDao
    }

    single { provideDatabase(application = androidApplication()) }
    single { provideDao(database = get()) }
}


