package com.salazar.cheers.map.ui.annotations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.UserProfilePicture

@Composable
fun FriendAnnotation(
    modifier: Modifier = Modifier,
    name: String,
    picture: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val color = when(isSelected) {
        true -> com.salazar.cheers.core.share.ui.BlueCheers
        false -> Color.White
    }

    val size = when(isSelected) {
        true -> 140.dp
        false -> 100.dp
    }

    Column(
        modifier = modifier
            .size(size),
    ) {
        UserProfilePicture(
            modifier = Modifier
                .border(2.dp, color, CircleShape)
                .weight(1f),
            picture = picture,
            onClick = onClick,
        )
        Text(
            text = name,
            modifier = Modifier
                .background(Color.White)
                .padding(),
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}