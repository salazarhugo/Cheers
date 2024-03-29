package com.salazar.cheers.feature.ticket.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.core.ui.ui.Toolbar
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType

@Composable
fun TicketDetailsScreen(
    uiState: TicketDetailsUiState,
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = navigateBack,
                title = "Ticket",
            )
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            if (uiState.ticket != null)
                Ticket(
                    ticket = uiState.ticket,
                )
        }
    }
}

@Composable
fun Ticket(
    ticket: Ticket,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = ticket.partyName,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = ticket.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ticket price",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = (ticket.price / 100).toString() + " EUR",
                style = MaterialTheme.typography.bodyMedium
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Barcode(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .size(160.dp),
                    type = BarcodeType.QR_CODE,
                    value = ticket.id
                )
                Text(
                    text = ticket.id,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
