package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.retainedComponent
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import org.example.project.component.DefaultRootComponent
import org.example.project.home.HomeScreen
import org.example.project.vm.TrackerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = retainedComponent {
            DefaultRootComponent(it)
        }
        setContent {
            App(root = root)
        }
        // create ViewModel
        val locationTracker = LocationTracker(
            permissionsController = PermissionsController(applicationContext = applicationContext)
        )
        val viewModel = TrackerViewModel(locationTracker)

// bind tracker to lifecycle
        viewModel.locationTracker.bind(this)
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}