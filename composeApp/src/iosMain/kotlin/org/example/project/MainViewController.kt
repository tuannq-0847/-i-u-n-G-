package org.example.project

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.example.project.component.DefaultRootComponent
import org.example.project.home.HomeScreen

fun MainViewController() = ComposeUIViewController {
    val root = remember {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry())
        )
    }
    App(root = root)
}