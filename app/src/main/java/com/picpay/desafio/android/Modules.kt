package com.picpay.desafio.android

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    single { get<Application>().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single { AppDatabase.getDatabase(get()) }
    single { get<AppDatabase>().userDao() }
    single { Retrofit.Builder()
        .baseUrl("https://609a908e0f5a13001721b74e.mockapi.io/picpay/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PicPayService::class.java)
    }
}
