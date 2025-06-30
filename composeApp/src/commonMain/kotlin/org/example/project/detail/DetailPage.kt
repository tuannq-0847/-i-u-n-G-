package org.example.project.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.LocationInfo
import kotlin.math.max
import kotlin.math.min

data class MenuItem(
    val name: String,
    val price: String,
    val imageRes: Int = 0 // Placeholder for image resource
)

data class TabItem(
    val title: String,
    val isSelected: Boolean
)

@Composable
fun DetailPage(
    locationInfo: LocationInfo,
    onGoBack: () -> Unit
) {
//    AsyncImage(
//        model = locationInfo.coverImage.orEmpty(),
//        contentDescription = "${locationInfo.name} image",
//        contentScale = ContentScale.Crop,
//        modifier = Modifier
//            .fillMaxSize()
//            .clip(MaterialTheme.shapes.medium)
//    )
    RestaurantMainPage()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMainPage() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    val tabs = listOf("Coffee", "Cake", "Cookie", "Tea")

    val famousItems = listOf(
        MenuItem("Coffee", "$5.49"),
        MenuItem("Cup Cake", "$7.49"),
        MenuItem("Croissant", "$4.99")
    )

    val menuItems = listOf(
        MenuItem("Coffee", "$5.49"),
        MenuItem("Coffee", "$5.49"),
        MenuItem("Coffee", "$5.49"),
        MenuItem("Coffee", "$5.49")
    )

    // Calculate scroll progress for collapsing effect
    val headerHeight = 300.dp
    val headerHeightPx = with(density) { headerHeight.toPx() }
    val toolbarHeight = 56.dp
    val toolbarHeightPx = with(density) { toolbarHeight.toPx() }

    val scrollOffset = listState.firstVisibleItemScrollOffset
    val firstVisibleItemIndex = listState.firstVisibleItemIndex

    // Calculate the scroll progress (0f to 1f)
    val scrollProgress = when {
        firstVisibleItemIndex > 0 -> 1f
        else -> min(1f, scrollOffset / (headerHeightPx - toolbarHeightPx))
    }

    // Calculate header height based on scroll
    val currentHeaderHeight = max(toolbarHeightPx, headerHeightPx - scrollOffset)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Item
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .background(Color(0xFFF8F8F8))
                ) {
                    // Restaurant Image with parallax effect
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(16.dp)
                            .graphicsLayer {
                                // Parallax effect - image moves slower than scroll
                                translationY = scrollOffset * 0.5f
                                alpha = 1f - (scrollProgress * 0.7f)
                            },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Restaurant image placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF8B4513)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Restaurant Interior",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    modifier = Modifier.alpha(1f - scrollProgress)
                                )
                            }
                        }
                    }

                    // Restaurant Info Card
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .graphicsLayer {
                                alpha = 1f - scrollProgress
                                translationY = scrollOffset * 0.3f
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        RestaurantInfoContent()
                    }
                }
            }

            // Content starts here
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F8F8))
                        .padding(top = 16.dp)
                ) {
                    // Direction Button
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Direction",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Image Gallery
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(4) { index ->
                            Card(
                                modifier = Modifier.size(80.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF8B4513)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${index + 1}",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Famous Section
                    Text(
                        "Famous",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Famous Items
            items(famousItems) { item ->
                MenuItemCard(item)
            }

            item {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Our Menu Section
                    Text(
                        "Our Menu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Menu Tabs
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(tabs) { index, tab ->
                            val isSelected = index == selectedTabIndex
                            Card(
                                modifier = Modifier
                                    .clickable { selectedTabIndex = index },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color.Black else Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    tab,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Menu Items
            items(menuItems) { item ->
                MenuItemCard(item)
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // Collapsing Toolbar
        TopAppBar(
            title = {
                Text(
                    "MoonBean's Coffee",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(scrollProgress)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.9f - (scrollProgress * 0.4f)),
                            CircleShape
                        )
                        .size(40.dp)
                ) {
//                    Icon(
//                        Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = Color.Black
//                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.9f - (scrollProgress * 0.4f)),
                            CircleShape
                        )
                        .size(40.dp)
                ) {
//                    Icon(
//                        Icons.Default.FavoriteBorder,
//                        contentDescription = "Favorite",
//                        tint = Color.Black
//                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF8B4513).copy(alpha = scrollProgress)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { currentHeaderHeight.toDp() })
        )
    }
}

@Composable
fun RestaurantInfoContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "MoonBean's Coffee",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    repeat(4) {
//                        Icon(
//                            Icons.Default.Star,
//                            contentDescription = null,
//                            tint = Color(0xFFFFD700),
//                            modifier = Modifier.size(16.dp)
//                        )
                    }
//                    Icon(
//                        Icons.Default.Star,
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier.size(16.dp)
//                    )
                    Text(
                        "4.7",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Icon(
//                        Icons.Default.LocationOn,
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier.size(16.dp)
//                    )
                    Text(
                        "Sector 7 Rohini, Delhi",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Icon(
//                        Icons.Default.Schedule,
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier.size(16.dp)
//                    )
                    Text(
                        "9:00 AM - 11:00 PM",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item Image
            Card(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF8B4513)),
                    contentAlignment = Alignment.Center
                ) {
//                    Icon(
//                        Icons.Default.Coffee,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Item Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Price and Add Button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(Color.Black, CircleShape)
                        .size(32.dp)
                ) {
//                    Icon(
//                        Icons.Default.Add,
//                        contentDescription = "Add",
//                        tint = Color.White,
//                        modifier = Modifier.size(16.dp)
//                    )
                }
            }
        }
    }
}
