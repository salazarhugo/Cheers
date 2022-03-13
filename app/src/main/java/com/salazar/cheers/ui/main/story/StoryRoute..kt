package com.salazar.cheers.ui.main.story

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Story screen.
 *
 * @param storyViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun StoryRoute(
    storyViewModel: StoryViewModel,
    navActions: CheersNavigationActions,
    showInterstitialAd: () -> Unit,
) {
    val uiState by storyViewModel.uiState.collectAsState()
    val context = LocalContext.current

    StoryScreen(
        uiState = uiState,
        onStoryClick = {},
        onStoryOpen = storyViewModel::onStoryOpen,
        onNavigateBack = { navActions.navigateBack() },
        onUserClick = { navActions.navigateToOtherProfile(it) } ,
        value =  uiState.input,
        onInputChange = storyViewModel::onInputChange,
        onSendReaction = storyViewModel::onSendReaction,
        pause = uiState.pause,
        onFocusChange = storyViewModel::onPauseChange,
        showInterstitialAd = showInterstitialAd,
    )
}