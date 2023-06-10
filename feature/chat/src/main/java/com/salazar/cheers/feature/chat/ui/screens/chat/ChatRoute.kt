package com.salazar.cheers.feature.chat.ui.screens.chat

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.share.ui.Permission
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.common.ui.NoScreenshot


/**
 * Stateful composable that displays the Navigation route for the Chat screen.
 *
 * @param chatViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
    showSnackBar: (String) -> Unit,
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null)
                chatViewModel.sendImageMessage(listOf(it))
        }

    if (!uiState.errorMessage.isNullOrBlank()) {
        LaunchedEffect(Unit) {
            showSnackBar(uiState.errorMessage!!)
        }
    }

    val micInteractionSource = remember { MutableInteractionSource() }
    val isPressed by micInteractionSource.collectIsPressedAsState()

    if (isPressed) {
        Permission(permission = Manifest.permission.RECORD_AUDIO) {
            Permission(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                SideEffect {
                    chatViewModel.startRecording()
                }
                DisposableEffect(Unit) {
                    onDispose {
                        chatViewModel.stopRecording()
                    }
                }
            }
        }
    }

    NoScreenshot()

    val channel = uiState.channel
    if (channel == null)
        LoadingScreen()
    else
        ChatScreen(
            uiState = uiState,
            onChatUIAction = { action ->
                when (action) {
                    is ChatUIAction.OnReplyMessage -> chatViewModel.onReplyMessage(action.message)
                    is ChatUIAction.OnLikeClick -> chatViewModel.likeMessage(action.messageId)
                    is ChatUIAction.OnUnLikeClick -> chatViewModel.unlikeMessage(action.messageId)
                    is ChatUIAction.OnSwipeRefresh -> TODO()
                    is ChatUIAction.OnUserClick -> navActions.navigateToOtherProfile(action.userId)
                    is ChatUIAction.OnBackPressed -> navActions.navigateBack()
                    is ChatUIAction.OnUnSendMessage -> chatViewModel.unsendMessage(action.messageId)
                    is ChatUIAction.OnSendTextMessage -> chatViewModel.sendTextMessage(action.text)
                    is ChatUIAction.OnImageSelectorClick -> launcher.launch("image/*")
                    is ChatUIAction.OnCopyText -> TODO()
                    is ChatUIAction.OnTextInputChange -> chatViewModel.onTextChanged(action.text)
                    is ChatUIAction.OnRoomInfoClick -> navActions.navigateToRoomDetails(action.roomId)
                }
            },
        )
}