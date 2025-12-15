package com.picpay.desafio.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.picpay.desafio.android.R

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val users by viewModel.users.observeAsState()
    val isLoading by viewModel.loading.observeAsState(initial = false)
    val hasError by viewModel.error.observeAsState()

    if (isLoading) {
        // Use the CircularProgressIndicator composable for loading states in Compose
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (hasError != null) {
        ErrorState() // Um Composable que mostra uma mensagem de erro
    } else {
        // Se não está carregando e não há erro, mostramos a lista de usuários.
        users?.let { userList ->
            UserList(users = userList) // Um Composable para exibir a lista
        }
    }
}

@Composable
fun ErrorState(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        // It's a good practice to use string resources
        Text(text = stringResource(id = R.string.error))
    }
}