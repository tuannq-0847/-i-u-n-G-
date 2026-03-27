package org.example.project.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.icerock.moko.geo.compose.BindLocationTrackerEffect
import dev.icerock.moko.geo.compose.LocationTrackerAccuracy
import dev.icerock.moko.geo.compose.rememberLocationTrackerFactory
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import dev.icerock.moko.permissions.location.LOCATION
import io.github.ahmad_hamwi.compose.pagination.PaginatedLazyColumn
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.LocationCardItem
import org.example.project.component.ScreenHomeComponent
import org.example.project.data.SearchRequest
import org.example.project.extension.CafeListSkeleton
import org.example.project.vm.VM
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun HandleTrackLocation(vm: VM, controller: PermissionsController) {
    val factoryLoc = rememberLocationTrackerFactory(LocationTrackerAccuracy.Best)
    val locationTracker = remember { factoryLoc.createLocationTracker(controller) }
    BindLocationTrackerEffect(locationTracker)
    LaunchedEffect(locationTracker) {
        locationTracker.startTracking()
        vm.tracker(locationTracker)
        vm.needCall = true
        vm.paginationState.refresh(1)
        locationTracker.stopTracking()
    }
}

@Composable
fun RequestPermissionAndTrack(
    vm: VM,
    controller: PermissionsController,
) {
//    val coroutineScope = rememberCoroutineScope()
    var permissionGranted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        try {
            controller.providePermission(Permission.LOCATION)
            controller.providePermission(Permission.COARSE_LOCATION)
            vm.checkGrantedLocation(controller)
            permissionGranted = true
        } catch (_: DeniedAlwaysException) {
            // Handle always denied
            permissionGranted = false
        } catch (_: DeniedException) {
            // Handle denied
            permissionGranted = false
        }
    }

    if (permissionGranted) {
        HandleTrackLocation(vm, controller)
    }
}

@Composable
@ExperimentalMaterial3Api
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    },
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier.pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = onRefresh),
        contentAlignment = contentAlignment
    ) {
        content()
        indicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeScreen(component: ScreenHomeComponent, vm: VM = viewModel { VM() }) {
    var expanded by remember { mutableStateOf(false) }
    var expandedStyle by remember { mutableStateOf(false) }
    var expandedLocation by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("Cafe") }
    var textStyle by remember { mutableStateOf("Style") }
    var textLocation by remember { mutableStateOf(getListLocation()[0]) }
//    var date by remember { vm.listData }
    var query by remember { mutableStateOf(vm.searchText.value) }
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val safeInsets = WindowInsets.safeDrawing // or safeContent
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val topDp = with(density) { safeInsets.getTop(density).toDp() }
    val bottomDp = with(density) { safeInsets.getBottom(density).toDp() }
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val controller: PermissionsController =
        remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)
    RequestPermissionAndTrack(vm, controller)
    val refresh = remember { vm.isRefreshing }
    Surface(
        modifier = Modifier.fillMaxSize().clickable(
            // important: must make this clickable to detect outside tap
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusManager.clearFocus() // Hide keyboard
        },
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            onRefresh = {
                vm.searchText.value = ""
                searchJob?.cancel()
                refresh.value = true
                vm.paginationState.refresh(1)
            },
            isRefreshing = refresh.value,
        ) {
            Column(modifier = Modifier.padding(top = topDp, bottom = bottomDp)) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Column() {
                        SearchView(
                            query,
                            onQueryChange = { newQuery ->
                                query = newQuery
                                vm.searchText.value = newQuery
                                searchJob?.cancel()
                                searchJob = coroutineScope.launch {
                                    delay(600)
                                    if (query.isNotEmpty()) {
                                        vm.paginationState.refresh(1)
                                    }
                                }
                                // Optionally, you can call a function to filter the list based on the query
                                // vm.filterList(query) // Uncomment if you have a filter function
                            },
                            onDidClear = {
                                vm.paginationState.refresh(1)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "Search..."
                        )
                        Row {
                            Column {
                                Button(
                                    onClick = { expanded = !expanded },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(text, fontWeight = FontWeight.Bold)
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Restaurant") },
                                        onClick = {
                                            text = "Restaurant"
                                            expanded = false
                                            vm.fetchPlaces(
                                                textLocation.getLocationString(),
                                                "restaurants"
                                            )
                                            textStyle = "Style"
                                        },
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Cafe") },
                                        onClick = {
                                            text = "Cafe"
                                            expanded = false
//                                            vm.fetchPlaces(textLocation.getLocationString())
                                            textStyle = "Style"
                                        }
                                    )
                                }
                            }
                            Column {
                                Button(
                                    onClick = { expandedStyle = !expandedStyle },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(textStyle, fontWeight = FontWeight.Bold)
                                }
                                DropdownMenu(
                                    modifier = Modifier.height(200.dp),
                                    expanded = expandedStyle,
                                    onDismissRequest = { expandedStyle = false }
                                ) {
                                    val list =
                                        if (text == "Cafe") getListCafeCategory() else foodList
                                    for (item in list) {
                                        DropdownMenuItem(
                                            text = { Text(item) },
                                            onClick = {
                                                textStyle = item
                                                expandedStyle = false
                                                vm.filterList(item)
                                            },
                                        )
                                    }
                                }
                            }
                            Column {
                                Button(
                                    onClick = { expandedLocation = !expandedLocation },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(textLocation, fontWeight = FontWeight.Bold)
                                }
                                DropdownMenu(
                                    expanded = expandedLocation,
                                    onDismissRequest = { expandedLocation = false }
                                ) {
                                    val list = getListLocation()
                                    for (item in list) {
                                        DropdownMenuItem(
                                            text = { Text(item) },
                                            onClick = {
                                                textStyle =
                                                    "Style" // Reset style when changing location
                                                textLocation = item
                                                expandedLocation = false
                                                vm.fetchPlaces(
                                                    textLocation.getLocationString(),
                                                    if (text.isRestaurant()) "restaurants" else "places"
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                PaginatedLazyColumn(
                    paginationState = vm.paginationState,
                    modifier = Modifier.fillMaxSize().clipToBounds(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    firstPageProgressIndicator = { CafeListSkeleton(10) },
                    newPageProgressIndicator = {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                ) {
                    items(vm.paginationState.allItems.orEmpty()) { location ->
                        LocationCardItem(component, locationInfo = location)
                    }
                }
//            if (vm.loading.value) {
//                CafeListSkeleton(10)
//            } else {
////                PlaceScreen(
////                    vm,
////                    component,
////                    date,
////                    onRetry = {
//////                showContent = false // Reset content state to show loading again
//////                vm.fetchPlaces() // Retry fetching data
////                    }
////                )
//            }
            }
        }
    }
}

fun getListCafeCategory() = listOf(
    "Artisan",
    "Bohemian",
    "Chic",
    "Cozy",
    "Craft",
    "Eclectic",
    "Industrial",
    "Minimalist",
    "Modern",
    "Nature",
    "Rustic",
    "Trendy",
    "Vintage"
)

val foodList = listOf(
    "Beefsteak",
    "Bistro",
    "Brunch",
    "Bánh mì",
    "Chè",
    "Cà ri",
    "Món Thái",
    "Nướng",
    "Phá lấu",
    "Ramen",
    "Spaghetti",
    "Sushi",
    "Dimsum",
    "Miền Tây",
    "Món Chay",
    "Món Hoa",
    "Món Huế",
    "Món Hàn",
    "Món Nhật",
    "Đá bào"

)


fun getListLocation() = listOf(
    "Hà Nội",
    "Hồ Chí Minh"
)

fun String.getLocationString(): String {
    return when (this) {
        "Hà Nội" -> "in.(Hà Nội)"
        "Hồ Chí Minh" -> "in.(Hồ Chí Minh)"
        else -> this
    }
}

fun String.isRestaurant(): Boolean {
    return this == "Restaurant"
}

@Composable
fun SearchView(
    query: String,
    onQueryChange: (String) -> Unit,
    onDidClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search..."
) {
    val focusRequester = remember { FocusRequester() }
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp).focusRequester(focusRequester)
            .focusable(true),
        singleLine = true,

        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            // Handle search button press here if needed
            // e.g., hide keyboard or call search callback
        }),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                if (query.isEmpty()) {
                    Text("🔍", fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp)) // search
                }

                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }

                if (query.isNotEmpty()) {
                    Text("❌", fontSize = 18.sp, modifier = Modifier.clickable {
                        onQueryChange("")
                        onDidClear()
                    })
                }
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

