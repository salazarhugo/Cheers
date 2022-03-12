package com.salazar.cheers.ui.main.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.salazar.cheers.data.db.Story
import com.salazar.cheers.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class StoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val input: String = "",
    val interstitialAd: InterstitialAd? = null,
    val pause: Boolean = false,
    val storiesFlow: Flow<PagingData<Story>>? = null,
)

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(StoryUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            viewModelState.value
        )

    init {
        refreshStoryFlow()
    }

    fun onPauseChange(pause: Boolean) {
        viewModelState.update {
            it.copy(pause = pause)
        }
    }

    private fun setNativeAd(interstitialAd: InterstitialAd?) {
        viewModelState.update {
            it.copy(interstitialAd = interstitialAd)
        }
    }

    fun onSendReaction(
        story: Story,
        text: String
    ) {
        storyRepository.sendReaction(story.author, text)
        onInputChange("")
    }

    fun onInputChange(input: String) {
        viewModelState.update {
            it.copy(input = input)
        }
    }

    fun onStoryOpen(storyId: String) {
        viewModelScope.launch {
            storyRepository.seenStory(storyId)
        }
    }

    private fun refreshStoryFlow() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelState.update {
            it.copy(storiesFlow = storyRepository.getStories(), isLoading = false)
        }
    }

}

