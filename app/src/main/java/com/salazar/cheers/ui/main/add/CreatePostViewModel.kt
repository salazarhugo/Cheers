package com.salazar.cheers.ui.main.add

import android.app.Application
import android.net.Uri
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.accompanist.pager.PagerState
import com.mapbox.common.BillingServiceErrorCode
import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.workers.CreatePostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

enum class CreatePostPage {
    CreatePost, ChooseOnMap, ChooseBeverage, AddPeople, DrunkennessLevel
}

data class CreatePostUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val drunkenness: Int = 0,
    val caption: String = "",
    val postType: String = PostType.TEXT,
    val photos: List<Uri> = emptyList(),
    val locationPoint: Point? = null,
    val location: String = "",
    val locationResults: List<SearchResult> = emptyList(),
    val selectedLocation: SearchResult? = null,
    val selectedTagUsers: List<UserItem> = emptyList(),
    val privacyState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val privacy: Privacy = Privacy.FRIENDS,
    val allowJoin: Boolean = true,
    val notify: Boolean = true,
    val page: CreatePostPage = CreatePostPage.CreatePost,
    val profilePictureUrl: String? = null,
    val drinkState: PagerState = PagerState(),
    val drinks: List<Beverage> = emptyList(),
)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    application: Application,
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatePostUiState(isLoading = false))
    private val workManager = WorkManager.getInstance(application)

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("photoUri")?.let {
            addPhoto(Uri.parse(it))
        }
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            viewModelState.update {
                it.copy(profilePictureUrl = user.picture)
            }
        }
        viewModelState.update {
            val drinks = Beverage.values().toList().sortedBy { it.displayName }
//                .filter { it.displayName.isNotBlank() }
            it.copy(drinks = drinks)
        }
    }


    fun selectPrivacy(privacy: Privacy) {
        viewModelState.update {
            it.copy(privacy = privacy)
        }
    }

    fun selectTagUser(user: UserItem) {
        val l = viewModelState.value.selectedTagUsers.toMutableList()
        if (l.contains(user)) l.remove(user) else l.add(user)
        viewModelState.update {
            it.copy(selectedTagUsers = l.toList())
        }
    }

    fun unselectLocation() {
        viewModelState.update {
            it.copy(selectedLocation = null)
        }
    }

    fun onDrunkennessChange(drunkenness: Int) {
        viewModelState.update {
            it.copy(drunkenness = drunkenness)
        }
    }

    fun selectLocation(location: SearchResult) {
        viewModelState.update {
            it.copy(selectedLocation = location)
        }
    }

    fun toggleNotify(notify: Boolean) {
        viewModelState.update {
            it.copy(notify = notify)
        }
    }

    fun toggleAllowJoin(allowJoin: Boolean) {
        viewModelState.update {
            it.copy(allowJoin = allowJoin)
        }
    }

    fun updateErrorMessage(errorMessage: String) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    fun updatePage(page: CreatePostPage) {
        viewModelState.update {
            it.copy(page = page)
        }
    }

    fun updateLocationPoint(point: Point) {
        viewModelState.update {
            it.copy(locationPoint = point)
        }
    }

    fun updateLocation(location: String) {
        viewModelState.update {
            it.copy(location = location)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun updateLocationResults(results: List<SearchResult>) {
        viewModelState.update {
            it.copy(locationResults = results)
        }
    }

    fun onCaptionChanged(caption: String) {
        viewModelState.update {
            it.copy(caption = caption)
        }
    }

    fun addPhoto(photo: Uri) {
        viewModelState.update {
            it.copy(photos = it.photos + photo, postType = PostType.IMAGE)
        }
    }

    fun setPhotos(photos: List<Uri>) {
        viewModelState.update {
            it.copy(photos = photos, postType = PostType.IMAGE)
        }
    }

    var uploadWorkerState: Flow<WorkInfo>? = null
    val id = mutableStateOf<UUID?>(null)

    fun uploadPost() {
        val uiState = viewModelState.value
        updateIsLoading(true)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val drink = uiState.drinks[uiState.drinkState.currentPage].displayName

        val uploadWorkRequest =
            OneTimeWorkRequestBuilder<CreatePostWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "PHOTOS" to uiState.photos.map { it.toString() }.toTypedArray(),
                        "POST_TYPE" to uiState.postType,
                        "PHOTO_CAPTION" to uiState.caption,
                        "DRUNKENNESS" to uiState.drunkenness,
                        "BEVERAGE" to drink,
                        "LOCATION_NAME" to uiState.selectedLocation?.name,
                        "LOCATION_LATITUDE" to uiState.locationPoint?.latitude(),
                        "LOCATION_LONGITUDE" to uiState.locationPoint?.longitude(),
                        "TAG_USER_IDS" to uiState.selectedTagUsers.map { it.id }.toTypedArray(),
                        "PRIVACY" to uiState.privacy.name,
                        "NOTIFY" to uiState.notify,
                    )
                )
                .setConstraints(constraints)
                .build()

        id.value = uploadWorkRequest.id

        workManager.enqueueUniqueWork(
            "post_upload",
            ExistingWorkPolicy.REPLACE,
            uploadWorkRequest,
        )

        uploadWorkerState = workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).asFlow()
        updateIsLoading(false)
    }
}

sealed class CreatePostUIAction {
    object OnBackPressed : CreatePostUIAction()
    object OnSwipeRefresh : CreatePostUIAction()
    data class OnSelectDrink(val drink: Beverage) : CreatePostUIAction()
}
