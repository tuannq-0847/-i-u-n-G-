package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.example.project.component.DefaultRootComponent

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TesProject",
    ) {
        val lifecycle = LifecycleRegistry()

        // Always create the root component outside Compose on the UI thread
        val root =
            DefaultRootComponent(
                componentContext = DefaultComponentContext(lifecycle = lifecycle),
            )

        App(root = root)
    }
}