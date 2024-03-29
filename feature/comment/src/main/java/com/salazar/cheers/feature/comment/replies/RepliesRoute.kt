package com.salazar.cheers.feature.comment.replies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun RepliesRoute(
    repliesViewModel: RepliesViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by repliesViewModel.uiState.collectAsStateWithLifecycle()

    RepliesScreen(
        uiState = uiState,
        onComment = repliesViewModel::onComment,
        profilePictureUrl = uiState.user?.picture ?: "",
        onInputChange = repliesViewModel::onInputChange,
        onBackPressed = { navActions.navigateBack() },
        onSwipeRefresh = { repliesViewModel.onSwipeRefresh() },
        onRepliesUIAction = { action ->
            when(action) {
                RepliesUIAction.OnBackPressed -> TODO()
                RepliesUIAction.OnFriendRequestsClick -> TODO()
                RepliesUIAction.OnSwipeRefresh -> TODO()
                is RepliesUIAction.OnUserClick -> TODO()
                is RepliesUIAction.OnCommentLongClick -> navActions.navigateToCommentMoreSheet(action.commentID)
                RepliesUIAction.OnRemoveReplyComment -> TODO()
                is RepliesUIAction.OnReplyClick -> TODO()
                is RepliesUIAction.OnCommentLike -> repliesViewModel.onLike(action.commentID)
            }
        }
    )
}