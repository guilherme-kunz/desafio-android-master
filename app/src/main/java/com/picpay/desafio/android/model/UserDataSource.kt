package com.picpay.desafio.android.model

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface UserDataSource {
    suspend fun getUsers(): List<User>
}

class UserDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : UserDataSource {

    override suspend fun getUsers(): List<User> {
        try {
            return httpClient.get {
                url(baseUrl + "users")
                contentType(ContentType.Application.Json)
            }.body()
        } catch (e: Exception) {
            throw e
        }
    }
}