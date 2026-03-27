package org.example.project.component

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import org.example.project.data.LocationInfo

class ScreenHomeComponent(
    componentContext: ComponentContext,
    private val onGoFullScreen: (LocationInfo, Int) -> Unit,
    private val onGoFullPage: (LocationInfo) -> Unit
) : ComponentContext by componentContext, RootComponent {

//    val jsonString = MutableValue("")

    fun onImageClick(location: LocationInfo, pos: Int) {
//        jsonString.value = value
        onGoFullScreen(location, pos)
    }

    fun onPageClick(location: LocationInfo) {
//        jsonString.value = value
        onGoFullPage(location)
    }
}
