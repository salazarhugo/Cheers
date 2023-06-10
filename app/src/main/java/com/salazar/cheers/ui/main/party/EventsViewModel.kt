package com.salazar.cheers.ui.main.party

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val query: String = "",
    val parties: List<Party>? = null,
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val partyRepository: PartyRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EventsUiState(isLoading = true))
    private var searchJob: Job? = null

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            partyRepository.feedParty(1, 10).collect {
                updateParties(it)
            }
        }
        viewModelScope.launch {
            val result = partyRepository.fetchFeedParty(1, 10)
            result.onFailure {
                updateError("Couldn't refresh party feed")
            }
        }
    }

    private fun updateError(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message, isLoading = false)
        }
    }

    private fun updateParties(parties: List<Party>?) {
        viewModelState.update {
            it.copy(parties = parties, isLoading = false)
        }
    }

    fun onQueryChange(query: String) {
        viewModelState.update {
            it.copy(query = query)
        }
        queryEvents(query = query)
    }

    private fun queryEvents(
        query: String = uiState.value.query.lowercase(),
        fetchFromRemote: Boolean = true,
    ) {
    }

    fun onGoingToggle(party: Party) {
        viewModelScope.launch {
        }
    }

    fun onInterestedToggle(party: Party) {
        viewModelScope.launch {
        }
    }

    fun onErrorMessageChange(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }
}

