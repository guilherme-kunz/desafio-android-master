package com.picpay.desafio.android.service

sealed class NetworkResponse<out T> {

    data class Success<T>(val data: T) : NetworkResponse<T>()
    data class Error(val error: Throwable) : NetworkResponse<Nothing>()
}