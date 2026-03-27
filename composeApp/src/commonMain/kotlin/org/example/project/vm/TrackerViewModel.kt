package org.example.project.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TrackerViewModel(
    val locationTracker: LocationTracker
) : ViewModel() {

    init {
        viewModelScope.launch {
            locationTracker.getLocationsFlow()
                .distinctUntilChanged()
                .collect { println("new location: $it") }
        }
    }

    fun onStartPressed() {
        viewModelScope.launch { locationTracker.startTracking() }
    }

    fun onStopPressed() {
        locationTracker.stopTracking()
    }
}