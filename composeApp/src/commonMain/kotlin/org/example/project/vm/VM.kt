package org.example.project.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import dev.icerock.moko.permissions.location.LOCATION
import io.github.ahmad_hamwi.compose.pagination.PaginationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.example.project.data.Error
import org.example.project.data.LocationInfo
import org.example.project.data.PlaceSearchResponse
import org.example.project.data.SearchRequest
import org.example.project.extension.mapToLocationList
import org.example.project.network.NetworkUtil
import org.example.project.network.PlaceApiService

class VM(
    private val placeApiService: PlaceApiService = PlaceApiService(
        NetworkUtil.httpClient,
        NetworkUtil.httpClient2
    )
) : ViewModel() {

    val listData = mutableStateOf<List<LocationInfo>>(emptyList())
    private val _listData = mutableStateOf<List<LocationInfo>>(emptyList())

    val listData2 = mutableStateOf<PlaceSearchResponse>(PlaceSearchResponse())
    private val _listData2 = mutableStateOf<PlaceSearchResponse>(PlaceSearchResponse())
    val error = mutableStateOf<Error>(Error("No error"))
    val loading = mutableStateOf<Boolean>(false)
    var job: Job? = null
    val isRefreshing = mutableStateOf(false)


    var latLng: LatLng? = null
    var needCall: Boolean? = null
    var switchCallProvider: Boolean = false

    var searchText = mutableStateOf("")

    suspend fun tracker(locationTracker: LocationTracker) {
        latLng = locationTracker.getLocationsFlow().firstOrNull()
    }

    suspend fun checkGrantedLocation(controller: PermissionsController) {
        needCall =
            !(controller.isPermissionGranted(Permission.LOCATION) || controller.isPermissionGranted(
                Permission.COARSE_LOCATION
            ))
    }

    val paginationState = PaginationState(
        initialPageKey = 1,
        onRequestPage = { pageKey: Int ->
            if (needCall == true) {
                job?.cancel()
            }
            job = CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                val items = fetchCF(
                    location = "in.(Hà Nội)",
                    req = SearchRequest(
                        pageKey,
                        12,
                        lat = latLng?.latitude,
                        lng = latLng?.longitude,
                        q = searchText.value.ifEmpty { null }
                    ),
                )
                if (isRefreshing.value) isRefreshing.value = false
                appendPage(
                    items,
                    nextPageKey = pageKey + 1,
                    isLastPage = items.size < 12
                )
            }
        })

    fun fetchPlaces(location: String = "in.(Hà Nội)", endpoint: String = "places") {
        viewModelScope.launch {
            try {
                loading.value = true
                val places = placeApiService.getPlaces(location, endpoint)
                delay(500)
                listData.value = places
                _listData.value = places
                loading.value = false
            } catch (e: Exception) {
                loading.value = false
                error.value = Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun fetchCF(
        location: String = "in.(Hà Nội)",
        req: SearchRequest,
        endpoint: String = "brands"
    ): List<LocationInfo> {
        delay(1000)
        val places = placeApiService.getCFPlaces(location, req, endpoint)
        return places.mapToLocationList()
    }

    private fun resetOriginal() {
        listData.value = _listData.value
    }

    fun filterList(tag: String) {
        resetOriginal()
        listData.value = listData.value.filter { it.tags?.contains(tag, true) == true }
    }
}