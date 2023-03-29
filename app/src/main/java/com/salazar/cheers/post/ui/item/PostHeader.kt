package com.salazar.cheers.post.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.data.enums.StoryState
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.relativeTimeFormatter
import com.salazar.cheers.ui.compose.Username
import com.salazar.cheers.ui.compose.share.UserProfilePicture

@Composable
fun PostHeader(
    username: String,
    verified: Boolean,
    beverage: Beverage,
    public: Boolean,
    createTime: Long,
    locationName: String,
    picture: String?,
    darkMode: Boolean = false,
    onHeaderClicked: (username: String) -> Unit = {},
    onMoreClicked: () -> Unit = {},
) {
    val color = if (darkMode) Color.White else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClicked(username) }
            .padding(16.dp, 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserProfilePicture(
                picture = picture,
                storyState = StoryState.EMPTY,
                size = 33.dp,
            )
            Spacer(Modifier.width(8.dp))
            Column(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Username(
                        username = username,
                        verified = verified,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        color = color,
                    )
                    if (beverage != Beverage.NONE) {
                        Text(
                            text = " is drinking ${beverage.displayName.lowercase()}",
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (locationName.isNotBlank())
                    Text(text = locationName, style = MaterialTheme.typography.labelSmall)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = relativeTimeFormatter(epoch = createTime),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 8.dp),
                color = color,
            )
            if (public)
                Icon(
                    Icons.Default.Public,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            Icon(
                Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onMoreClicked() },
                tint = color
            )
        }
    }
}