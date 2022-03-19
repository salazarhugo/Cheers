package com.salazar.cheers.components.post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.salazar.cheers.R
import com.salazar.cheers.components.LikeButton
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.ui.main.home.TagUsers

@Composable
fun PostFooter(
    postFeed: PostFeed,
    onLike: (post: Post) -> Unit,
    navigateToComments: (PostFeed) -> Unit,
    pagerState: PagerState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {

        if (postFeed.post.photos.size > 1)
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                activeColor = MaterialTheme.colorScheme.primary,
            )
//        Text(postFeed.post.tagUsersId.toString())
//        Text(postFeed.tagUsers.toString())
        PostFooterButtons(
            postFeed.post,
            onLike = onLike,
            navigateToComments = { navigateToComments(postFeed) },
        )
        if (postFeed.post.type != PostType.TEXT) {
//            LikedBy(post = postFeed.post)
            if (postFeed.tagUsers.isNotEmpty())
                TagUsers(postFeed.tagUsers)
        }
    }
    if (postFeed.post.type != PostType.TEXT)
        Spacer(Modifier.height(12.dp))
}

@Composable
fun PostFooterButtons(
    post: Post,
    onLike: (post: Post) -> Unit,
    navigateToComments: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LikeButton(
                like = post.liked,
                likes = post.likes,
                onToggle = { onLike(post) },
            )
            Icon(
                modifier = Modifier.clickable { navigateToComments(post.id) },
                painter = rememberImagePainter(R.drawable.ic_bubble_icon),
                contentDescription = null
            )
            Icon(Icons.Outlined.Share, null)
        }
        if (post.drunkenness > 0)
            DrunkennessLevelIndicator(drunkenness = post.drunkenness)
    }
}

@Composable
fun DrunkennessLevelIndicator(
    drunkenness: Int,
) {
    var show by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .clickable { show = !show }
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (show)
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "Drunkenness level out of 5",
                style = MaterialTheme.typography.bodyMedium,
            )
        else {
            Icon(
                Icons.Outlined.WaterDrop,
                contentDescription = null,
            )
//            Image(
//                painter = rememberImagePainter(R.drawable.ic_tequila_shot),
//                modifier = Modifier.size(24.dp),
//                contentDescription = null,
//            )
            Text(
                text = ((drunkenness * 5).toFloat() / 100f).toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
//        for (i in 1..5) {
//            val icon =
//                when {
//                    drunkenness*5/100 >= i -> Icons.Default.Star
//                    drunkenness*5/100 > i - 1 + 0.5 -> Icons.Default.StarHalf
//                    else -> Icons.Default.StarOutline
//                }
//            Icon(
//                icon,
//                contentDescription = null,
//                tint = Color(0xFFFFD700)
//            )
//        }
    }
}
