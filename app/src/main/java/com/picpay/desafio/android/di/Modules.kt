package com.picpay.desafio.android.di

import android.app.Application
import androidx.room.Room
import com.picpay.desafio.android.database.AppDatabase
import com.picpay.desafio.android.database.dao.UsersDao
import com.picpay.desafio.android.model.UserDataSource
import com.picpay.desafio.android.model.UserDataSourceImpl
import com.picpay.desafio.android.repository.Repository
import com.picpay.desafio.android.repository.RepositoryImpl
import com.picpay.desafio.android.service.RetrofitBuilder
import com.picpay.desafio.android.ui.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val networkModule = module {

    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }
    single<UserDataSource> {
        UserDataSourceImpl(
            get(),
            "https://609a908e0f5a13001721b74e.mockapi.io/picpay/api/"
        )
    }
}
val dispatchersModule = module {
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }
}
val viewModelModule = module {
    viewModel {
        MainViewModel(
            repository = get(),
            ioDispatcher = get(named("io"))
        )
    }
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


