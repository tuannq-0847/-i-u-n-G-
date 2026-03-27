package org.example.project.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import org.example.project.data.LocationInfo

class DetailImageComponent(
    componentContext: ComponentContext,
    val locationInfo: LocationInfo,
    val pos: Int,
    private val onGoBack: () -> Unit
) : ComponentContext by componentContext, RootComponent{

    fun goBack(){
        onGoBack()
    }
}