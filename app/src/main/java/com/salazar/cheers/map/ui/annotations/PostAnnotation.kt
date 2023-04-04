package com.salazar.cheers.map.ui.annotations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.ui.compose.animations.Bounce
import com.salazar.cheers.ui.compose.share.UserProfilePicture
import com.salazar.cheers.ui.theme.BlueCheers

@Composable
fun PostAnnotation(
    modifier: Modifier = Modifier,
    post: Post,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val color = when(isSelected) {
        true -> BlueCheers
        false -> Color.White
    }

    val size = when(isSelected) {
        true -> 140.dp
        false -> 100.dp
    }

    val picture = when(post.photos.isEmpty()) {
        true -> post.profilePictureUrl
        false -> post.photos.first()
    }

    Column(
        modifier = modifier
            .size(size),
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Bounce(
                onBounce = onClick,
            ) {
                AsyncImage(
                    model = picture,
                    contentDescription = null,
                    modifier = modifier
                        .border(2.dp, color, MaterialTheme.shapes.medium)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_beer),
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .offset(x = 8.dp, y = 8.dp)
                ,
            )
        }
        Text(
            text = post.username,
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