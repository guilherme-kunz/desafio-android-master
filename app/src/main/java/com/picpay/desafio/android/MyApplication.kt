package com.picpay.desafio.android

import android.app.Application
import com.picpay.desafio.android.di.repositoryModule
import com.picpay.desafio.android.di.userDateBuilder
import com.picpay.desafio.android.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    viewModelModule,
                    repositoryModule,
                    userDateBuilder
                )
            )
        }
    }
}