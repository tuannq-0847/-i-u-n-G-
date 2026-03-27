package org.example.project.component

import com.arkivanov.decompose.ComponentContext
import org.example.project.data.LocationInfo

class DetailPageComponent(
    componentContext: ComponentContext,
    val locationInfo: LocationInfo,
    private val onGoBack: () -> Unit
) : ComponentContext by componentContext, RootComponent {

    fun goBack() {
        onGoBack()
    }
}