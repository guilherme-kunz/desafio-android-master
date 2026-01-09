package com.picpay.desafio.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.picpay.desafio.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {

    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val hasError by viewModel.error.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.verifyNetwork(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Contatos") },
                modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.Green
                    )
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