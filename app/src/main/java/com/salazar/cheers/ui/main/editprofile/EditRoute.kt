package com.salazar.cheers.ui.main.editprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Edit profile screen.
 *
 * @param editProfileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun EditProfileRoute(
    editProfileViewModel: EditProfileViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by editProfileViewModel.uiState.collectAsStateWithLifecycle()

    EditProfileScreen(
        uiState = uiState,
        onWebsiteChanged = editProfileViewModel::onWebsiteChanged,
        onNameChanged = editProfileViewModel::onNameChanged,
        onBioChanged = editProfileViewModel::onBioChanged,
        onUsernameChanged = editProfileViewModel::onUsernameChange,
        onSelectImage = editProfileViewModel::onSelectPicture,
        onDismiss = { navActions.navigateBack() },
        onSave = {
            editProfileViewModel.updateUser()
            navActions.navigateBack()
        },
    )
}