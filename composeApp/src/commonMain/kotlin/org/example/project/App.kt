package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.ahmad_hamwi.compose.pagination.PaginatedLazyColumn
import org.example.project.component.DefaultRootComponent
import org.example.project.component.DetailImageComponent
import org.example.project.component.DetailPageComponent
import org.example.project.component.ScreenHomeComponent
import org.example.project.data.LocationInfo
import org.example.project.detail.DetailImageScreen
import org.example.project.detail.DetailPage
import org.example.project.extension.toVND
import org.example.project.full_screen.FullScreenImages
import org.example.project.home.HomeScreen
import org.example.project.vm.VM
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(vm: VM = viewModel { VM() }, root: DefaultRootComponent) {
    MaterialTheme {
//        Children(
//            stack = root.childStack,
//            modifier = Modifier.fillMaxSize(),
////            animation = stackAnimation { child, otherChild, direction ->
////                when (child.instance) {
////                    is DetailImageComponent, is ScreenHomeComponent -> null
////                    else -> slide()
////                }
////            }
//        ) { child ->
//            // Render your screens based on child.instance
//            when (val component = child.instance) {
//                is ScreenHomeComponent -> {
//                    // Always render home screen as base layer
//                    HomeScreen(component, vm)
//                }
//
//                is DetailImageComponent -> {
//                    DetailImageScreen(component.locationInfo, component, component.pos)
//                }
//
//                is DetailPageComponent -> {
//                    DetailPage(component.locationInfo,component)
//                }
//            }
//        }
        val childStack by root.childStack.subscribeAsState()
        Box(modifier = Modifier.fillMaxSize()) {
            // Render all screens, with overlays on top
            childStack.items.forEach { child ->
                when (val component = child.instance) {
                    is ScreenHomeComponent -> {
                        // Always render home screen as base layer
                        HomeScreen(component, vm)
                    }

                    is DetailImageComponent -> {
                        // Only render if it's the current (top) screen
                        if (child == childStack.active) {
                            DetailImageScreen(component.locationInfo, component, component.pos)
                        }
                    }

                    is DetailPageComponent -> {
                        AnimatedVisibility(
                            visible = child == childStack.active, // 🔥 this MUST be dynamic!
                            enter = slideInHorizontally(
                                initialOffsetX = { it }, // slide in from right
                                animationSpec = tween(300)
                            ),
                            exit = slideOutHorizontally(
                                targetOffsetX = { -it }, // slide out to left
                                animationSpec = tween(300)
                            )
                        ) {
                            DetailPage(component.locationInfo, component)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PlaceScreen(
    vm: VM, // Pass the state, typically from a ViewModel
    component: ScreenHomeComponent,
    data: List<LocationInfo>,
    onRetry: () -> Unit     // Lambda for retry action){}){}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (data.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("No locations found. Try refreshing!")
            }
        } else {
            LocationList(locations = data, component, vm)
        }
    }
}

@Composable
fun LocationList(locations: List<LocationInfo>, component: ScreenHomeComponent, vm: VM) {
    val listState = rememberLazyListState()
    // Scroll to top when state changes
    LaunchedEffect(vm.paginationState.allItems) {
        if (vm.paginationState.allItems?.isNotEmpty() == true) listState.scrollToItem(0) // Smooth scroll
        // or listState.scrollToItem(0) for instant scroll
    }
    PaginatedLazyColumn(
        paginationState = vm.paginationState,
        state = listState, // Add this line!
        modifier = Modifier.fillMaxSize().clipToBounds(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(locations) { location ->
            LocationCardItem(component, locationInfo = location)
        }
    }
}

@Composable
fun LocationCardItem(component: ScreenHomeComponent, locationInfo: LocationInfo) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            component.onPageClick(locationInfo)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Reduced elevation
        shape = MaterialTheme.shapes.medium, // Standard shape
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Use surface for cards
    ) {
        Column {
            // --- Images Section ---
            // --- Images Section with HorizontalPager and DotsIndicator ---
            FullScreenImages(locationInfo) {
                component.onImageClick(locationInfo, it)
            }

            // --- Details Section ---
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = locationInfo.name ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                InfoRow(label = "Address", text = locationInfo.location)
                InfoRow(label = "City", text = locationInfo.city ?: "", isSubtle = true)
                Spacer(modifier = Modifier.height(10.dp))

                if (!locationInfo.description.isNullOrBlank()) {
                    ExpandableText(
                        text = locationInfo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (!locationInfo.priceAvg.isNullOrBlank()) {
                    InfoRow(
                        label = "Average Price",
                        text = locationInfo.priceAvg.toVND(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (!locationInfo.openTime.isNullOrBlank() || !locationInfo.closeTime.isNullOrBlank()) {
                    val hours = mutableListOf<String>()
                    locationInfo.openTime?.takeIf { it.isNotBlank() }?.let { hours.add(it) }
                    locationInfo.closeTime?.takeIf { it.isNotBlank() }?.let { hours.add(it) }
                    InfoRow(label = "Hours", text = hours.joinToString(" - "))
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // --- Social & Contact Section ---
                val socialLinksPresent = listOfNotNull(
                    locationInfo.socialPhone, locationInfo.socialWebsite,
                    locationInfo.socialFacebook, locationInfo.socialInstagram,
                    locationInfo.socialX, locationInfo.socialMenu
                ).any { it.isNotBlank() }

                if (socialLinksPresent) {
                    SectionTitle(title = "Contact & Links")
                }

                if (!locationInfo.socialPhone.isNullOrBlank()) {
                    ClickableInfoRow(
                        label = "Call: ${locationInfo.socialPhone}",
                        onClick = { /* TODO: Implement call intent for locationInfo.socialPhone */

                        }
                    )
                }
                if (!locationInfo.socialWebsite.isNullOrBlank()) {
                    ClickableInfoRow(
                        label = "Visit Website",
                        onClick = {
                            try {
                                uriHandler.openUri(locationInfo.socialWebsite)
                            } catch (e: Exception) {
                            }
                        }
                    )
                }
                if (!locationInfo.socialFacebook.isNullOrBlank()) {
                    ClickableInfoRow(
                        label = "Facebook",
                        onClick = {
                            try {
                                uriHandler.openUri(locationInfo.socialFacebook)
                            } catch (e: Exception) {
                            }
                        }
                    )
                }
                if (!locationInfo.socialInstagram.isNullOrBlank()) {
                    ClickableInfoRow(
                        label = "Instagram",
                        onClick = {
                            try {
                                uriHandler.openUri(locationInfo.socialInstagram)
                            } catch (e: Exception) {
                            }
                        }
                    )
                }
                if (!locationInfo.socialX.isNullOrBlank()) {
                    ClickableInfoRow(
                        label = "X (Twitter)",
                        onClick = {
                            try {
                                uriHandler.openUri(locationInfo.socialX)
                            } catch (e: Exception) {
                            }
                        }
                    )
                }
                if (!locationInfo.socialMenu.isNullOrBlank()) {
                    ClickableInfoRow(
                        label = "View Menu",
                        onClick = {
                            try {
                                uriHandler.openUri(locationInfo.socialMenu)
                            } catch (e: Exception) {
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium, // Made title a bit larger
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
}

// InfoRow now takes an optional label which will be bolded
@Composable
fun InfoRow(
    label: String? = null,
    text: String,
    isSubtle: Boolean = false,
    labelStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), // Bolder label
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    if (text.isNotBlank()) {
        Row(
            verticalAlignment = Alignment.Top, // Align to top for multi-line text
            modifier = Modifier.padding(vertical = 3.dp) // Increased vertical padding
        ) {
            label?.let {
                Text(
                    text = "$it: ",
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isSubtle) 0.7f else 0.9f),
                    modifier = Modifier.widthIn(max = 100.dp) // Give label some space
                )
            }
            Text(
                text = text,
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isSubtle) 0.7f else 0.9f)
            )
        }
    }
}

@Composable
fun ClickableInfoRow(
    label: String, // This label is now the clickable text
    linkColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    if (label.isNotBlank()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = linkColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .fillMaxWidth() // Make the whole row area potentially clickable
                .clickable(onClick = onClick)
                .padding(vertical = 6.dp) // Increased padding
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(items: List<String>) {
    if (items.isNotEmpty()) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Increased spacing
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { item ->
                SuggestionChip( // Using SuggestionChip for a slightly different, often more subtle look
                    onClick = { /* Optional: Handle chip click */ },
                    label = { Text(item, style = MaterialTheme.typography.labelMedium) }
                    // Icon parameter removed
                )
            }
        }
    }
}


@Composable
fun ExpandableText(
    text: String,
    style: TextStyle,
    color: Color,
    collapsedMaxLines: Int = 3
) {
    var expanded by remember { mutableStateOf(false) }
    var textLayoutResult by remember {
        mutableStateOf<androidx.compose.ui.text.TextLayoutResult?>(
            null
        )
    }
    val isExpandable = textLayoutResult?.hasVisualOverflow ?: false

    Column {
        Text(
            text = text,
            style = style,
            color = color,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult = it }
        )
        if (isExpandable && !expanded) {
            Text(
                text = "Read more",
                style = style.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { expanded = true }
                    .align(Alignment.End)
                    .padding(top = 2.dp)
            )
        } else if (expanded) {
            Text(
                text = "Read less",
                style = style.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { expanded = false }
                    .align(Alignment.End)
                    .padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun DotsIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    dotSize: androidx.compose.ui.unit.Dp = 8.dp,
    spacing: androidx.compose.ui.unit.Dp = 4.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPage == iteration) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}


//// --- Preview ---
//@Preview(showBackground = true, name = "Full Location Card Item Preview")
//@Composable
//fun FullLocationCardItemPreview() {
//    val sampleLocation = LocationInfo(
//        id = 1,
//        name = "The Globe Trotter's Rest & Roastery",
//        description = "An eclectic cafe offering beans from around the world. Perfect for travelers and dreamers alike. We also have a small library and offer vegan pastries. This description is a bit longer to test the 'read more' functionality and see how it behaves when it overflows the initial line limit.",
//        location = "123 Wanderlust Lane, Old Quarter",
//        city = "Hanoi",
//        rating = "4.9",
//        tagsString = "Coffee, Global, Cozy, Books, Vegan, Travel, Artisan",
//        servicesString = "Free Ultra-Fast Wi-Fi, Pet-friendly Patio, Laptop Charging Ports, Workshops",
//        imagesResponseString = "https://images.didau.date/523-story/0-Cafe-image-0.webp, https://images.didau.date/523-story/1-Cafe-image-1.webp, https://images.didau.date/523-story/2-Cafe-image-2.webp",
//        lat = 21.028511,
//        longitude = 105.804817,
//        openTime = "07:00",
//        closeTime = "23:00",
//        socialPhone = "+84 123 456 789",
//        socialWebsite = "https://globetrotters.example.com",
//        socialFacebook = "https://facebook.com/globetrotters",
//        socialInstagram = "https://instagram.com/globetrotters",
//        socialX = "https://x.com/globetrotters",
//        socialMenu = "https://globetrotters.example.com/menu"
//    )
//    YourAppTheme { // Replace YourAppTheme with your actual app theme
//        LazyColumn(contentPadding = PaddingValues(16.dp)) {
//            item {
//                LocationCardItem(locationInfo = sampleLocation)
//            }
//        }
//    }
//}