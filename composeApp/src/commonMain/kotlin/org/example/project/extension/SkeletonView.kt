package org.example.project.extension

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Shimmer effect modifier
fun Modifier.shimmerEffect(
    shape: Shape = RoundedCornerShape(4.dp),
    baseColor: Color = Color.Gray.copy(alpha = 0.3f),
    highlightColor: Color = Color.White.copy(alpha = 0.6f)
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "translate"
    )

    background(baseColor, shape).then(
        Modifier.clip(shape)
    ).drawWithContent {
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                highlightColor,
                Color.Transparent
            ),
            start = Offset(translateAnim - 300, 0f),
            end = Offset(translateAnim, size.height)
        )

        drawContent()
        drawRect(brush = brush)
    }
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    Box(modifier = modifier.shimmerEffect(shape = shape))
}

// Skeleton for the dot indicator (image carousel)
@Composable
fun SkeletonDotIndicator(
    dotCount: Int = 8,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) {
            SkeletonBox(
                modifier = Modifier.size(6.dp),
                shape = CircleShape
            )
        }
    }
}

// Main cafe card skeleton
@Composable
fun CafeCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Image placeholder with rounded corners
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dot indicator for image carousel
            SkeletonDotIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cafe name
            SkeletonBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(24.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Address section
            Row(
                verticalAlignment = Alignment.Top
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(200.dp)
                            .height(14.dp),
                        shape = RoundedCornerShape(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // City
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .width(35.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(6.dp))
                SkeletonBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hours
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .width(45.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(2.dp)
                )
            }
        }
    }
}

// Skeleton for the bottom card (sweetbrew.cafe style)
@Composable
fun BottomCafeCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Background image skeleton
            SkeletonBox(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            )

            // Overlay content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Cafe name in cursive style
                SkeletonBox(
                    modifier = Modifier
                        .width(180.dp)
                        .height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                )
            }
        }
    }
}

// Complete skeleton list for multiple cards
@Composable
fun CafeListSkeleton(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(itemCount) { index ->
            if (index == itemCount - 1) {
                // Last item as bottom style card
                BottomCafeCardSkeleton()
            } else {
                // Regular cafe card
                CafeCardSkeleton()
            }
        }
    }
}

// Alternative compact version
@Composable
fun CompactCafeCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Small image
            SkeletonBox(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Name
                SkeletonBox(
                    modifier = Modifier
                        .width(140.dp)
                        .height(18.dp),
                    shape = RoundedCornerShape(3.dp)
                )

                // Address
                SkeletonBox(
                    modifier = Modifier
                        .width(180.dp)
                        .height(12.dp),
                    shape = RoundedCornerShape(2.dp)
                )

                // Rating and hours
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp),
                        shape = RoundedCornerShape(2.dp)
                    )
                    SkeletonBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp),
                        shape = RoundedCornerShape(2.dp)
                    )
                }
            }
        }
    }
}

// Usage example
@Composable
fun CafeSkeletonExamples() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Cafe Cards Skeleton",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            // Full detail cards
            items(3) {
                CafeCardSkeleton()
            }

            item {
                Text(
                    "Compact Version",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Compact cards
            items(2) {
                CompactCafeCardSkeleton()
            }

            // Bottom style card
            item {
                BottomCafeCardSkeleton()
            }
        }
    }
}