package com.salazar.cheers.ui.main.story.feed

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.ui.CheersAppState

/**
 * Stateful composable that displays the Navigation route for the StoryFeed screen.
 *
 * @param storyFeedViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun StoryFeedRoute(
    appState: CheersAppState,
    storyFeedViewModel: StoryFeedViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by storyFeedViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val errorMessage = uiState.errorMessage

    if (errorMessage != null) {
        LaunchedEffect(appState.snackBarHostState) {
            appState.showSnackBar(errorMessage)
        }
    }

//    SetStoryStatusBars()

    StoryFeedScreen(
        uiState = uiState,
        onStoryFeedUIAction = { action ->
            when (action) {
                is StoryFeedUIAction.OnBackPressed -> navActions.navigateBack()
                is StoryFeedUIAction.OnDelete -> {}
                is StoryFeedUIAction.OnActivity -> {}
                is StoryFeedUIAction.OnViewed -> storyFeedViewModel.onViewed(action.storyId)
                is StoryFeedUIAction.OnToggleLike -> storyFeedViewModel.onToggleLike(action.storyId, action.liked)
                is StoryFeedUIAction.OnMoreClick -> navActions.navigateToStoryMoreSheet(action.storyId)
                else -> {}
            }
        },
    )
}

@Composable
fun SetStoryStatusBars() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val systemUiController = rememberSystemUiController()
    val darkIcons = !isSystemInDarkTheme()
    val background = MaterialTheme.colorScheme.background

    DisposableEffect(lifecycleOwner) {
        systemUiController.setSystemBarsColor(
            color = Color.Black,
            darkIcons = false,
        )
        onDispose {
            systemUiController.setSystemBarsColor(
                if (darkIcons) Color.White else background,
                darkIcons = darkIcons
            )
        }
    }
}
