package com.salazar.cheers.ui.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


sealed class ProfileSheetUIAction {
    object OnNfcClick : ProfileSheetUIAction()
    object OnCopyProfileClick : ProfileSheetUIAction()
    object OnSettingsClick : ProfileSheetUIAction()
    object OnAddSnapchatFriends : ProfileSheetUIAction()
    object OnPostHistoryClick : ProfileSheetUIAction()
}

@Composable
fun ProfileMoreBottomSheet(
    onProfileSheetUIAction: (ProfileSheetUIAction) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline)
        )
        Item(
            text = "Settings",
            icon = Icons.Outlined.Settings,
            onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnSettingsClick) }
        )
        Item(
            text = "Add Snapchat Friends",
            icon = Icons.Outlined.Archive,
            onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnAddSnapchatFriends) }
        )
        Item(
            text = "Post History",
            icon = Icons.Outlined.Archive,
            onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnPostHistoryClick) }
        )
        Item(
            text = "Nfc",
            icon = Icons.Outlined.Contactless,
            onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnNfcClick) }
        )
        Item(text = "QR code", icon = Icons.Outlined.QrCode)
        Item(text = "Saved", icon = Icons.Outlined.BookmarkBorder)
        Item(
            text = "Copy Profile URL",
            icon = Icons.Outlined.ContentCopy,
            onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnCopyProfileClick) }
        )
    }
}

@Composable
fun Item(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(22.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
