package com.salazar.cheers.feature.settings.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class PaymentHistoryViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(PaymentHistoryUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

}

data class PaymentHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val payments: List<String> = emptyList(),
)

