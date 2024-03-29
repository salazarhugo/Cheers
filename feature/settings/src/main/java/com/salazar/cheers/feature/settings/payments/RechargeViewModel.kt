package com.salazar.cheers.feature.settings.payments

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.data.user.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RechargeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(RechargeUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            accountRepository.getAccount().onSuccess {
                updateCoins(it.coins)
            }
        }
        billingRepository.startConnection()
        refreshSkuDetails()
    }


    private fun updateCoins(coins: Int) {
        viewModelState.update {
            it.copy(coins = coins)
        }
    }

    fun refreshSkuDetails() {
        viewModelScope.launch {
            val productDetails = billingRepository.queryProductDetails().productDetailsList
            if (productDetails != null) {
                viewModelState.update {
                    it.copy(skuDetails = productDetails.sortedBy { it.oneTimePurchaseOfferDetails?.priceAmountMicros })
                }
            }
        }
    }

    fun onProductClick(
        productDetails: ProductDetails,
        activity: Activity
    ) {
        billingRepository.launchBillingFlow(activity, productDetails)
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

}

data class RechargeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val coins: Int = 0,
    val skuDetails: List<ProductDetails>? = null,
)

