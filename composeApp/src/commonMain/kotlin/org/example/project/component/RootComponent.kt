package org.example.project.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.Serializable
import org.example.project.data.LocationInfo

interface RootComponent

class DefaultRootComponent(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {
    // This class can be extended to provide default implementations or properties
    // for the RootComponent interface if needed.
    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.Home(),
        handleBackButton = true,
        childFactory = ::createChildComponent
    )

    private fun createChildComponent(
        configuration: Configuration,
        componentContext: ComponentContext
    ): RootComponent = when (configuration) {
        is Configuration.Home -> ScreenHomeComponent(componentContext, onGoFullPage = { loc ->
            navigation.pushNew(Configuration.DetailPage(loc))
        }, onGoFullScreen = { loc, pos ->
            navigation.pushNew(Configuration.DetailImage(loc, pos))
        })

        is Configuration.DetailImage -> DetailImageComponent(
            componentContext = componentContext,
            configuration.locationInfo,
            configuration.pos,
            onGoBack = { navigation.pop() }
        )

        is Configuration.DetailPage -> DetailPageComponent(
            componentContext = componentContext,
            configuration.locationInfo, onGoBack = { navigation.pop() })
    }

    @Serializable

    sealed class Configuration {
        @Serializable
        class Home() : Configuration()

        @Serializable
        data class DetailImage(val locationInfo: LocationInfo, val pos: Int) : Configuration()

        @Serializable
        data class DetailPage(val locationInfo: LocationInfo) : Configuration()
    }
}