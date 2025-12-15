package com.picpay.desafio.android.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.picpay.desafio.android.model.User

@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserListItem(user = user)
        }
    }
}