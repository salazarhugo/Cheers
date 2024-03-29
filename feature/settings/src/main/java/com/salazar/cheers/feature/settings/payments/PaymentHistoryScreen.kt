package com.salazar.cheers.feature.settings.payments

//@Composable
//fun PaymentHistoryScreen(
//    onBackPressed: () -> Unit,
//    payments: List<Payment>
//) {
//    Scaffold(
//        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Coin history") },
//    ) {
//        LazyColumn() {
//            items(payments) {
//                PaymentHistoryItem(
//                    payment = it
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun PaymentHistoryItem(
//    payment: Payment,
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(
//            text = (payment.amount?.div(100)).toString() + " " + payment.currency?.toUpperCase(),
//            style = MaterialTheme.typography.bodyMedium
//        )
//        Text(
//            text = payment.status.capitalize(),
//            style = MaterialTheme.typography.bodyMedium
//        )
//        val dateFormatter = SimpleDateFormat("dd/MM HH:mm")
//        val createTime = Date(payment.createTime * 1000)
//        Text(
//            text = dateFormatter.format(createTime),
//            style = MaterialTheme.typography.bodyMedium
//        )
//    }
//}
