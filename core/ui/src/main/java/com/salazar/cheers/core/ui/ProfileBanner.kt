package com.salazar.cheers.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.salazar.cheers.core.util.Utils.conditional

@Composable
fun ProfileBanner(
    modifier: Modifier = Modifier,
    banner: String? = null,
    clickable: Boolean = false,
    alpha: Float = 1f,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier
                .aspectRatio(3f)
                .alpha((alpha - 0.7f) / 0.3f)
                .blur(
                    radius = 50.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                ),
            model = banner,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        AsyncImage(
            modifier = Modifier
                .aspectRatio(3f)
                .alpha((alpha - 0.7f) / 0.3f)
                .background(Color.DarkGray)
                .conditional(clickable) {
                    this.clickable {
                        onClick()
                    }
                    .background(Color.Black)
                    .alpha(0.6f)
                },
            model = banner,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        if (clickable)
            Icon(Icons.Default.Edit, contentDescription = null)
    }
}