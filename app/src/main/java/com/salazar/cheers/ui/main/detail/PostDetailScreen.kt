package com.salazar.cheers.ui.main.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.geojson.Point
import com.salazar.cheers.R
import com.salazar.cheers.ui.compose.items.UserItem
import com.salazar.cheers.post.ui.item.LikeButton
import com.salazar.cheers.post.ui.item.PostBody
import com.salazar.cheers.post.ui.item.PostHeader
import com.salazar.cheers.post.ui.PostText
import com.salazar.cheers.user.ui.FollowButton
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.core.data.internal.Privacy
import com.salazar.cheers.ui.compose.utils.PrettyImage
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.core.data.util.Utils.isDarkModeOn
import java.util.*

@Composable
fun PostDetailScreen(
    uiState: PostDetailUiState.HasPost,
    onBackPressed: () -> Unit,
    onHeaderClicked: (username: String) -> Unit,
    onDelete: () -> Unit,
    onLeave: () -> Unit,
    onMessageClicked: () -> Unit,
    onMapClick: () -> Unit,
    onToggleLike: (Post) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val post = uiState.postFeed
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(title = { 
                Text(
                text = stringResource(id = R.string.post),
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto)
              },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "")
                    }
                })
        }
    ) {
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(top = it.calculateTopPadding()),
        ) {
            Post(
                post = uiState.postFeed,
                members = uiState.members,
                onHeaderClicked = onHeaderClicked,
                onDelete = onDelete,
                isAuthor = post.authorId == FirebaseAuth.getInstance().currentUser?.uid!!,
                onMapClick = onMapClick,
                onToggleLike = onToggleLike,
                onLeave = onLeave,
                onMessageClicked = onMessageClicked,
                onUserClick = onUserClick,
            )
            PrivacyText(post.privacy)
        }
    }
}

@Composable
fun PrivacyText(
    privacy: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = privacy,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun StaticMap(
    modifier: Modifier = Modifier,
    longitude: Double,
    latitude: Double,
    onMapClick: () -> Unit,
) {
    val context = LocalContext.current
    val style =
        if (context.isDarkModeOn())
            StaticMapCriteria.DARK_STYLE
        else
            StaticMapCriteria.LIGHT_STYLE

    val token = stringResource(R.string.mapbox_access_token)
    val staticImage = remember {
        MapboxStaticMap.builder()
            .accessToken(token)
            .styleId(style)
            .cameraPoint(Point.fromLngLat(longitude, latitude)) // Image's center point on map
            .staticMarkerAnnotations(
                listOf(
                    StaticMarkerAnnotation.builder().lnglat(Point.fromLngLat(longitude, latitude))
                        .build()
                )
            )
            .cameraZoom(13.0)
            .width(640)
            .height(640)
            .retina(true)
            .build()
    }

    val url = remember { staticImage.url().toString() }
    PrettyImage(
        modifier = modifier
            .clickable { onMapClick() },
        data = url,
    )
}

@Composable
fun PostDetails(
    privacy: Privacy,
    createTime: Long,
    drunkenness: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sharing with",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(privacy.icon, null)
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(privacy.title)
                    Text(privacy.subtitle)
                }
            }
            Spacer(Modifier.height(32.dp))
            Text(
                text = Date(createTime.toLong()).toString(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Drunkenness level $drunkenness",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun Post(
    post: Post,
    members: List<UserItem>?,
    onHeaderClicked: (username: String) -> Unit,
    onLeave: () -> Unit,
    onDelete: () -> Unit,
    onUserClick: (String) -> Unit,
    onMapClick: () -> Unit,
    onMessageClicked: () -> Unit,
    onToggleLike: (Post) -> Unit,
    isAuthor: Boolean,
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val joined = post.tagUsersId.contains(uid)

    Column {
        LazyColumn {

            item {
                PostHeader(
                    post = post,
                    public = post.privacy == Privacy.PUBLIC.name,
                )
                PostText(
                    caption = post.caption,
                    onUserClicked = onUserClick,
                    onPostClicked = {},
                )
                PostBody(
                    post = post,
                    onPostClicked = {},
                    onLike = {},
                    pagerState = rememberPagerState()
                )
                PostFooter(
                    post = post,
                    onDelete = onDelete,
                    isAuthor = isAuthor,
                    onToggleLike = onToggleLike,
                )
            }
            item {
                Text(
                    text = "Drinkers",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            if (members != null)
                items(members, key = { it.id }) { user ->
                    UserItem(
                        userItem = user,
                        onClick = { onUserClick(user.username) },
                    ) {
                        FollowButton(isFollowing = user.has_followed, onClick = {})
                    }
                }
            if (post.locationName.isNotBlank())
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = "Hangout event",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Column {
//                        Text(
//                            text = String.format("%.2f", post.locationLongitude),
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Text(
//                            text = String.format("%.2f", post.locationLatitude),
//                            style = MaterialTheme.typography.titleMedium
//                        )
                        }
                    }
                    StaticMap(
                        longitude = post.longitude,
                        latitude = post.latitude,
                        onMapClick = onMapClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .aspectRatio(1f),
                    )
                }
            item {
                PostDetails(
                    privacy = Privacy.valueOf(post.privacy),
                    createTime = post.createTime,
                    drunkenness = post.drunkenness,
                )
            }

            item {
                Buttons(
                    joined = joined,
                    onLeave = onLeave,
                    onMessageClicked = onMessageClicked
                )
            }
        }
    }
}

@Composable
fun Buttons(
    joined: Boolean,
    onLeave: () -> Unit,
    onMessageClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (joined) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onLeave,
            ) {
                Text("Leave")
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onMessageClicked,
        ) {
            Text("Message")
        }
    }
}

@Composable
fun PostFooter(
    post: Post,
    isAuthor: Boolean,
    onDelete: () -> Unit,
    onToggleLike: (Post) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LikeButton(
                like = post.liked,
                likes = post.likes,
                onToggle = { onToggleLike(post) })
            Icon(painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon), "")
            Icon(Icons.Outlined.Share, null)
        }
        if (isAuthor)
            Icon(
                Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.clickable { onDelete() },
                tint = MaterialTheme.colorScheme.error,
            )
    }
}

@Composable
fun VideoPlayer(
    uri: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Create media item
    val mediaItem = MediaItem.fromUri(uri)

    // Create the player
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(mediaItem)
            this.prepare()
            this.playWhenReady = true
            this.repeatMode = Player.REPEAT_MODE_ALL
            this.volume = 0f
            this.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                StyledPlayerView(context).apply {
                    this.player = player
                }
            },
            modifier = modifier.clickable {
                if (player.volume == 0f) player.volume = 1f else player.volume = 0f
            }
        ) {
            it.useController = false
            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        }
    ) {
        onDispose {
            player.release()
        }
    }
}
