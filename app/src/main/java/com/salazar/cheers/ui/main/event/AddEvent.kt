package com.salazar.cheers.ui.main.event

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.R
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.SwitchM3
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class AddEventFragment : DialogFragment() {

    private val viewModel: AddEventViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DialogFullScreen)
        reverseGeocoding = MapboxSearchSdk.getReverseGeocodingSearchEngine()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.Theme_Cheers_Slide)
        }
    }

    private lateinit var reverseGeocoding: ReverseGeocodingSearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    private val searchCallback = object : SearchCallback {

        override fun onResults(
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
            if (results.isEmpty()) {
                Log.i("SearchApiExample", "No reverse geocoding results")
            } else {
                Log.i("SearchApiExample", "Reverse geocoding results: $results")
                viewModel.updateLocationResults(results)
                viewModel.updateLocation("On Pin")
            }
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Reverse geocoding error", e)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CheersTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        AddEventScreen()
                    }
                }
            }
        }
    }

    @Composable
    fun TopAppBar() {
        SmallTopAppBar(
            title = { Text("New event", fontWeight = FontWeight.Bold, fontFamily = Roboto) },
            navigationIcon = {
                IconButton(onClick = { dismiss() }) {
                    Icon(Icons.Default.ArrowBack, "")
                }
            },
        )
    }

    @Composable
    fun AddEventScreen() {
//        val user = mainViewModel.user2.value
        val uiState = viewModel.uiState.collectAsState().value
        PrivacyBottomSheet(uiState = uiState) {
            Scaffold(
                topBar = { TopAppBar() },
            ) {
                Tabs(uiState = uiState)
            }
        }
    }

    @Composable
    fun Tabs(uiState: AddEventUiState) {
        val tabs = 4
        val pagerState = rememberPagerState()

        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                count = tabs,
                state = pagerState,
                modifier = Modifier.height(600.dp)
            ) { page ->
                Column(modifier = Modifier.fillMaxHeight()) {
                    when (page) {
                        0 -> FirstScreen(uiState = uiState)
                        1 -> {
                            Text("1 wd")
                        }
                        2 -> {
                            Text("2 wd")
                        }
                        3 -> {
                            Text("3 wd")
                        }
                    }
                }
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
            )
            ShareButton(uiState = uiState)
        }
    }

    @Composable
    fun FirstScreen(uiState: AddEventUiState) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AddPhotoOrVideo(uiState = uiState)
            NameTextField(uiState = uiState)
            DividerM3()
            StartDateInput(uiState = uiState)
            DividerM3()
            TagSection()
            DividerM3()
            LocationSection()
            DividerM3()
            Description(uiState = uiState)
            DividerM3()
            Privacy(uiState = uiState)
        }
    }

    @Composable
    fun Privacy(uiState: AddEventUiState) {
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    scope.launch {
                        uiState.privacyState.show()
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val privacy = uiState.selectedPrivacy
                Icon(privacy.icon, null)
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(privacy.title)
                    Text(privacy.subtitle)
                }
            }
            Icon(Icons.Filled.KeyboardArrowRight, null)
        }
    }

    @Composable
    fun PrivacyBottomSheet(
        uiState: AddEventUiState,
        content: @Composable () -> Unit
    ) {
        val state = uiState.privacyState
        ModalBottomSheetLayout(
            sheetElevation = 0.dp,
            sheetState = state,
            sheetBackgroundColor = MaterialTheme.colorScheme.background,
            sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            sheetContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    Text(
                        "Event privacy",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                    DividerM3()
                    Text(
                        "Choose who can see and join this event. You'll be able to invite people later.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    val scope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            scope.launch {
                                uiState.privacyState.hide()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Done")
                    }
                }
            }
        ) {
            content()
        }
    }

    @Composable
    fun Item(
        item: Privacy,
        selected: Boolean,
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    viewModel.selectPrivacy(privacy = item)
                }
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(item.icon, null)
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(item.title)
                    Text(item.subtitle)
                }
            }
            Checkbox(
                checked = selected,
                onCheckedChange = {
                    viewModel.selectPrivacy(privacy = item)
                },
            )
        }
    }

    @Composable
    fun StartDateInput(uiState: AddEventUiState) {
        val calendar = remember { Calendar.getInstance() }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        calendar.time = remember { Date() }
//        viewModel.onStartDateChange("$day/$month/$year")
//        viewModel.onStartTimeChange("$hourOfDay:$minute")
//        viewModel.onEndDateChange("$day/$month/$year")
//        viewModel.onEndTimeChange("$hourOfDay:$minute")

        val startDatePicker = DatePickerDialog(
            requireContext(), { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                viewModel.onStartDateChange(
                    "$year-${
                        (month + 1).toString().padStart(2, '0')
                    }-${dayOfMonth.toString().padStart(2, '0')}"
                )

            }, year, month, day
        )
        val endDatePicker = DatePickerDialog(
            requireContext(), { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                viewModel.onEndDateChange(
                    "$year-${
                        (month + 1).toString().padStart(2, '0')
                    }-${dayOfMonth.toString().padStart(2, '0')}"
                )
            }, year, month, day
        )
        val startTimePicker = TimePickerDialog(
            requireContext(), { _: TimePicker, hourOfDay: Int, minute: Int ->
                viewModel.onStartTimeChange(
                    "${
                        hourOfDay.toString().padStart(2, '0')
                    }:${minute.toString().padStart(2, '0')}"
                )
            }, hourOfDay, minute, true
        )

        val endTimePicker = TimePickerDialog(
            requireContext(), { _: TimePicker, hourOfDay: Int, minute: Int ->
                viewModel.onEndTimeChange(
                    "${
                        hourOfDay.toString().padStart(2, '0')
                    }:${minute.toString().padStart(2, '0')}"
                )
            }, hourOfDay, minute, true
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.offset(y = 12.dp))
                Column {
                    SwitchPreference(
                        value = uiState.allDay,
                        text = "All-day"
                    ) { viewModel.onAllDayChange(it) }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { startDatePicker.show() },
                            text = uiState.startDate,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (!uiState.allDay)
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable { startTimePicker.show() },
                                text = uiState.startTime,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { endDatePicker.show() },
                            text = uiState.endDate,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (!uiState.allDay)
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable { endTimePicker.show() },
                                text = uiState.endTime,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                    }
                }
            }
        }
    }

    @Composable
    fun DescriptionInput(uiState: AddEventUiState) {
        val description = uiState.description
        val focusManager = LocalFocusManager.current
        TextField(
            value = description,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            onValueChange = {
                viewModel.onDescriptionChange(it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            placeholder = { Text("Description") },
            enabled = !uiState.isLoading,
        )
    }

    @Composable
    fun NameTextField(uiState: AddEventUiState) {
        val eventName = uiState.name
        val focusManager = LocalFocusManager.current
        TextField(
            value = eventName,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(start = 36.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            onValueChange = {
                viewModel.onEventNameChange(it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            placeholder = { Text("Add title", style = MaterialTheme.typography.titleLarge) },
            enabled = !uiState.isLoading,
            trailingIcon = {
                val photoUri = uiState.imageUri
                if (photoUri != null) {
                    Image(
                        modifier = Modifier
                            .clickable(onClick = { openPhotoVideoChooser() })
                            .padding(horizontal = 16.dp)
                            .size(50.dp),
                        painter = rememberImagePainter(data = uiState.imageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        )
    }

    @Composable
    fun ShareButton(uiState: AddEventUiState) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            DividerM3()
            Button(
                onClick = {
                    viewModel.uploadEvent()
                    dismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = uiState.name.isNotBlank()
            ) {
                Text("Share")
            }
        }
    }

    @Composable
    fun SwitchPreference(
        value: Boolean,
        text: String,
        onCheckedChange: (Boolean) -> Unit = {},
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))
            SwitchM3(
                modifier = Modifier.background(Color.Red),
                checked = value,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    fun AddPhotoOrVideo(uiState: AddEventUiState) {
        if (uiState.imageUri != null)
            return

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            FilledTonalButton(onClick = { openPhotoVideoChooser() }) {
                Icon(Icons.Outlined.PhotoCamera, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add photo or video")
            }
        }
    }

    @Composable
    fun LocationSection() {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Outlined.LocationOn, null)
            Column {
                Row(
                    modifier = Modifier
                        .clickable {
                            findNavController().navigate(R.id.chooseOnMap)
                        }
                        .padding(start = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Outlined.MyLocation, null)
                        Text(
                            text = viewModel.location.value,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun LocationResultsSection(results: List<SearchResult>) {
        if (results.isNotEmpty())
            LocationResult(results = results)
    }

    @Composable
    fun LocationResult(results: List<SearchResult>) {
        ChipGroup(
            users = results.map { it.name },
            onSelectedChanged = { name ->
                val location = results.find { it.name == name }
                if (location != null)
                    viewModel.selectLocation(location)
            },
            unselectedColor = MaterialTheme.colorScheme.outline,
        )
    }

    @Composable
    fun SelectedLocation(location: SearchResult) {
        Row(
            modifier = Modifier
                .clickable {
                    findNavController().navigate(R.id.chooseOnMap)
                }
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.tertiary)
                Text(text = location.name, fontSize = 14.sp)
            }
            Icon(
                Icons.Outlined.Close,
                null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable {
                    viewModel.unselectLocation()
                }
            )
        }
    }

    @Composable
    fun Description(uiState: AddEventUiState) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Description, null)
            DescriptionInput(uiState = uiState)
        }
    }

    @Composable
    fun TagSection() {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Outlined.People, null)
            Column {
                Row(
                    modifier = Modifier
                        .clickable {
                            findNavController().navigate(R.id.tagUser)
                        }
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Add people",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                    )
                    val tagUsers = viewModel.selectedTagUsers
                    if (tagUsers.size == 1)
                        Text(
                            text = tagUsers[0].username,
                            style = MaterialTheme.typography.labelLarge
                        )
                    if (tagUsers.size > 1)
                        Text(
                            text = "${tagUsers.size} people",
                            style = MaterialTheme.typography.labelLarge
                        )
                }
            }
        }
    }

    @Composable
    fun CaptionSection(
        user: User,
        uiState: AddEventUiState
    ) {
        Row(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
//            val videoUri = uiState..value

//            if (videoUri != null && viewModel.postType.value == PostType.VIDEO)
//                VideoPlayer(
//                    uri = videoUri,
//                    modifier = Modifier
//                        .padding(bottom = 8.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .height(120.dp)
//                        .aspectRatio(9f / 16f)
//                )
//            else
            ProfilePicture(user)
            val caption = viewModel.caption

            TextField(
                value = caption.value,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 4.dp),
                onValueChange = {
                    viewModel.onCaptionChanged(it)
                },
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                placeholder = {
                    Text(text = "Write a caption...", fontSize = 13.sp)
                },
                trailingIcon = {
                    val photoUri = uiState.imageUri
                    if (photoUri != null) {
                        Image(
                            modifier = Modifier
                                .clickable(onClick = { openPhotoVideoChooser() })
                                .padding(horizontal = 16.dp)
                                .size(50.dp),
                            painter = rememberImagePainter(data = uiState.imageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
        }
    }

    private fun openPhotoVideoChooser() {
        Utils.openPhotoVideoChooser(singleImageResultLauncher)
    }

    private val singleImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageOrVideoUri: Uri = data?.data ?: return@registerForActivityResult

                val type = data.resolveType(requireContext()) ?: ""
                if (type.startsWith("image"))
                    viewModel.setImage(imageOrVideoUri)
            }
        }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    //            dismiss()
    companion object {
        private const val TAG = "AddDialogFragment"
    }

    @Composable
    fun VideoPlayer(
        uri: Uri,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current

        // Create media item
        val mediaItem = MediaItem.fromUri(uri)

        // Create the player
        val player = remember {
            ExoPlayer.Builder(context).build().apply {
                this.setMediaItem(mediaItem)
                this.prepare()
                this.playWhenReady = true
                this.repeatMode = Player.REPEAT_MODE_ALL
                this.volume = 0f
                this.videoScalingMode = VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
        }

        DisposableEffect(
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        this.player = player
                    }
                },
                modifier = modifier
            ) {
                it.useController = false
                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        ) {
            onDispose {
                player.release()
            }
        }
    }

    @Composable
    fun ProfilePicture(user: User) {
        Image(
            painter = rememberImagePainter(
                data = user.profilePictureUrl,
                builder = {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                },
            ),
            contentDescription = "Profile image",
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp),
            contentScale = ContentScale.Crop,
        )
    }

}
