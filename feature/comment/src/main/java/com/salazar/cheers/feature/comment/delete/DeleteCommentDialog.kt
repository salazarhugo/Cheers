package com.salazar.cheers.feature.comment.delete

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.core.ui.CoreDialog
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun DeleteCommentDialog(
    navActions: CheersNavigationActions,
    viewModel: DeleteCommentViewModel = hiltViewModel(),
) {
    CoreDialog(
        title = "Delete comment?",
        text = "Your comment will be permanently deleted.",
        dismissButton = stringResource (id = com.salazar.cheers.core.ui.R.string.cancel),
        onDismiss = {
            navActions.navigateBack()
        },
        confirmButton = stringResource(id = com.salazar.cheers.core.ui.R.string.delete),
        onConfirm = {
            viewModel.deleteComment {
                navActions.navigateBack()
            }
        },
    )
}