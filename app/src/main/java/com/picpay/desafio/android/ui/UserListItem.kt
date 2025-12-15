package com.picpay.desafio.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.picpay.desafio.android.model.User

@Composable
fun UserListItem(user: User) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = user.img,contentDescription = "Foto de ${user.name}",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = user.name, fontWeight = FontWeight.Bold)
            Text(text = user.username, style = MaterialTheme.typography.bodySmall)
        }
    }
}