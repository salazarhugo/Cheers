package com.salazar.cheers.components.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.share.UserProfilePicture
import com.salazar.cheers.internal.StoryState
import com.salazar.cheers.ui.theme.BlueCheers

@Composable
fun YourStory(
    profilePictureUrl: String?,
    storyState: StoryState,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            UserProfilePicture(
                avatar = profilePictureUrl ?: "",
                storyState = storyState,
                size = 64.dp,
                modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                onClick = onClick,
            )
            if (storyState == StoryState.EMPTY)
                Box(
                    modifier = Modifier
                        .offset(x = (-6).dp, y = (-6).dp)
                        .size(22.dp)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .background(BlueCheers)
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        modifier = Modifier.padding(3.dp),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your story",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
fun Story(
    modifier: Modifier = Modifier,
    seenStory: Boolean,
    profilePictureUrl: String,
    onStoryClick: (String) -> Unit,
    username: String,
) {
    val state = if (seenStory) StoryState.SEEN else StoryState.NOT_SEEN

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserProfilePicture(
            avatar = profilePictureUrl,
            storyState = state,
            size = 64.dp,
            modifier = Modifier.padding(horizontal = 8.dp),
            onClick = { onStoryClick(username) },
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = username,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
