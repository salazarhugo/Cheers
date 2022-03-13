package com.salazar.cheers.ui.main.add

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User
import com.salazar.cheers.workers.UploadPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

enum class AddPostPage {
    AddPost, ChooseOnMap, ChooseBeverage, AddPeople
}

data class AddPostUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val name: String = "",
    val caption: String = "",
    val beverage: String = "",
    val postType: String = PostType.TEXT,
    val photos: List<Uri> = emptyList(),
    val locationPoint: Point? = null,
    val location: String = "Current Location",
    val locationResults: List<SearchResult> = emptyList(),
    val selectedLocation: SearchResult? = null,
    val selectedTagUsers: List<User> = emptyList(),
    val privacyState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val privacy: Privacy = Privacy.FRIENDS,
    val allowJoin: Boolean = true,
    val page: AddPostPage = AddPostPage.AddPost,
)

@HiltViewModel
class AddPostViewModel @Inject constructor(application: Application) : ViewModel() {

    private val viewModelState = MutableStateFlow(AddPostUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    private val workManager = WorkManager.getInstance(application)

    fun selectPrivacy(privacy: Privacy) {
        viewModelState.update {
            it.copy(privacy = privacy)
        }
    }

    fun selectTagUser(user: User) {
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

    fun selectLocation(location: SearchResult) {
        viewModelState.update {
            it.copy(selectedLocation = location)
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

    fun updatePage(page: AddPostPage) {
        viewModelState.update {
            it.copy(page = page)
        }
    }

    fun onNameChanged(name: String) {
        viewModelState.update {
            it.copy(name = name)
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
        Log.d("HAHA", uiState.photos.toString())
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadPostWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "PHOTOS" to uiState.photos.map { it.toString() }.toTypedArray(),
                        "POST_TYPE" to uiState.postType,
                        "PHOTO_CAPTION" to uiState.caption,
                        "NAME" to uiState.name,
                        "LOCATION_NAME" to uiState.selectedLocation?.name,
                        "LOCATION_LATITUDE" to uiState.selectedLocation?.coordinate?.latitude(),
                        "LOCATION_LONGITUDE" to uiState.selectedLocation?.coordinate?.longitude(),
                        "TAG_USER_IDS" to uiState.selectedTagUsers.map { it.id }.toTypedArray(),
                        "PRIVACY" to uiState.privacy.name,
                        "ALLOW_JOIN" to uiState.allowJoin,
                    )
                )
            }
                .build()

        id.value = uploadWorkRequest.id
        workManager.enqueue(uploadWorkRequest)
        uploadWorkerState = workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).asFlow()
    }
}
