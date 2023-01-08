package com.salazar.cheers.ui.main.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.ActivityType
import com.salazar.cheers.internal.relativeTimeFormatter
import com.salazar.cheers.internal.toSentence
import com.salazar.cheers.ui.compose.CheersBadgeBox
import com.salazar.cheers.ui.compose.EmptyActivity
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.ui.compose.share.SwipeToRefresh
import com.salazar.cheers.ui.compose.share.UserProfilePicture
import com.salazar.cheers.ui.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.ui.compose.text.MyText
import com.salazar.cheers.ui.compose.user.FollowButton
import com.salazar.cheers.ui.main.party.create.TopAppBar


@Composable
fun ActivityScreen(
    uiState: ActivityUiState,
    onActivityUIAction: (ActivityUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onDismiss = { onActivityUIAction(ActivityUIAction.OnBackPressed)},
                title = "Activity"
            )
        }
    ) {
        if (uiState.isLoading)
            LoadingScreen()
        else
            SwipeToRefresh(
                state = rememberSwipeToRefreshState(uiState.isLoading),
                onRefresh = { onActivityUIAction(ActivityUIAction.OnSwipeRefresh)},
                modifier = Modifier.padding(it),
            ) {
                Column {
                    val activities = uiState.activities

                    if (activities?.isEmpty() == true)
                        EmptyActivity()
                    else
                        ActivityList(
                            uiState = uiState,
                            activities = activities,
                            onActivityUIAction = onActivityUIAction,
                        )
                }
            }
    }
}


@Composable
fun ActivityList(
    uiState: ActivityUiState,
    activities: List<Activity>?,
    onActivityUIAction: (ActivityUIAction) -> Unit,
) {
    LazyColumn {
        item {
            FriendRequests(
                count = uiState.friendRequestCounter,
                picture = uiState.friendRequestPicture,
                onClick = { onActivityUIAction(ActivityUIAction.OnFriendRequestsClick)},
            )
        }
        item {
            MyText(
                text = "This week",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
        if (activities != null)
            items(activities) {
                ActivityItem(
                    activity = it,
                    onActivityClick = { onActivityUIAction(ActivityUIAction.OnActivityClick(it))},
                )
            }
    }
}

@Composable
fun FriendRequests(
    count: Int? = null,
    picture: String? = null,
    onClick: () -> Unit,
) {
    if (count == null || count == 0)
        return

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheersBadgeBox(count = count) {
                UserProfilePicture(
                    picture = picture,
                    size = 40.dp,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column() {
                Text(
                    text = "Friend requests",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Approve or ignore requests",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
                )
            }
        }
    }
}

@Composable
fun ActivityItem(
    activity: Activity,
    onActivityClick: (Activity) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onActivityClick(activity) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserProfilePicture(
                picture = activity.avatar,
                size = 40.dp,
            )
            Spacer(modifier = Modifier.width(16.dp))
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(activity.username)
                }
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                    append(activity.type.toSentence())
                }
                append(" ")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ), fontWeight = FontWeight.Normal
                    )
                ) {
                    append(relativeTimeFormatter(epoch = activity.createTime))
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                softWrap = true,
            )
        }
        if (activity.type == ActivityType.FOLLOW)
            FollowButton(
                modifier = Modifier.padding(start = 16.dp),
                isFollowing = true,
                onClick = {},
            )
        else if (activity.type == ActivityType.POST_LIKE)
            Image(
                modifier = Modifier
                    .size(50.dp),
                painter = rememberAsyncImagePainter(model = activity.photoUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
    }
}
