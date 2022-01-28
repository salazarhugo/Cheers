package com.salazar.cheers.ui.editprofile

import androidx.compose.runtime.*
import com.salazar.cheers.navigation.CheersNavigationActions

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
    val uiState by editProfileViewModel.uiState.collectAsState()

    EditProfileScreen(
        uiState = uiState,
        onWebsiteChanged = editProfileViewModel::onWebsiteChanged,
        onNameChanged = editProfileViewModel::onNameChanged,
        onBioChanged = editProfileViewModel::onBioChanged,
        onSelectImage = editProfileViewModel::onSelectPicture,
        onDismiss = { navActions.navigateBack() },
        onSave = {
            editProfileViewModel.updateUser()
            navActions.navigateBack()
         } ,
    )
}