package org.example.project.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import coil3.compose.AsyncImage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.project.DotsIndicator
import org.example.project.component.DetailImageComponent
import org.example.project.data.LocationInfo
import org.example.project.extension.isVideo
import kotlin.math.absoluteValue


@Composable
fun DetailImageScreen(
    locationInfo: LocationInfo,
    component: DetailImageComponent,
    pos: Int = 0
) {
    // Remove Surface completely - it adds background
    Box(
        modifier = Modifier
            .fillMaxSize()// Fill entire screen
            .background(Color.Transparent)
            .clickable {
                // Optional: dismiss on background click
                component.goBack()
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .shadow(8.dp, shape = RoundedCornerShape(16.dp), clip = false) // Put shadow first
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 16.dp)
                .fillMaxWidth(0.95f) // 50% of parent's width
                .fillMaxHeight(0.75f)
                .clickable(enabled = false) { } // Prevent click through on content
        ) {

            IconButton(
                onClick = { component.goBack() },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp, bottom = 8.dp)
//                    .background(
//                        Color.Black.copy(alpha = 0.5f),
//                        CircleShape
//                    )
            ) {
                // Using a simple X drawn with paths (works on all platforms)
                Icon(
                    imageVector = ImageVector.Builder(
                        name = "Close",
                        defaultWidth = 24.dp,
                        defaultHeight = 24.dp,
                        viewportWidth = 24f,
                        viewportHeight = 24f
                    ).apply {
                        path(fill = SolidColor(Color.White)) {
                            moveTo(19f, 6.41f)
                            lineTo(17.59f, 5f)
                            lineTo(12f, 10.59f)
                            lineTo(6.41f, 5f)
                            lineTo(5f, 6.41f)
                            lineTo(10.59f, 12f)
                            lineTo(5f, 17.59f)
                            lineTo(6.41f, 19f)
                            lineTo(12f, 13.41f)
                            lineTo(17.59f, 19f)
                            lineTo(19f, 17.59f)
                            lineTo(13.41f, 12f)
                            close()
                        }
                    }.build(),
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp)
                )
            }
            if (locationInfo.imageUrlList.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { locationInfo.imageUrlList.size })
                LaunchedEffect(key1 = Unit) {
                    pagerState.scrollToPage(pos)
                }
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(),
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f) // Use weight instead of fillMaxSize
                ) { pageIndex ->
                    // Calculate scale based on page offset
                    val pageOffset =
                        (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                    val scale = lerp(
                        start = 0.7f, // Side images scale
                        stop = 1.0f,  // Center image scale
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    )

                    // Calculate alpha for fade effect
                    val alpha = lerp(
                        start = 0.6f, // Side images alpha
                        stop = 1.0f,  // Center image alpha
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    )
                    if (locationInfo.imageUrlList[pageIndex].isVideo()) {
                        val playerHost = remember {
                            MediaPlayerHost(mediaUrl = locationInfo.imageUrlList[pageIndex]) }

                        VideoPlayerComposable(
                            modifier = Modifier.fillMaxSize(),
                            playerHost = playerHost
                        )
                    } else {

                        AsyncImage(
                            model = locationInfo.imageUrlList[pageIndex],
                            contentDescription = "${locationInfo.name} image ${pageIndex + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(MaterialTheme.shapes.medium)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                }
                        )
                    }
                }

                // Dots Indicator
                if (pagerState.pageCount > 1) {
                    DotsIndicator(
                        pageCount = pagerState.pageCount,
                        currentPage = pagerState.currentPage,
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                }
            } else {
                // Placeholder for no image
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(200.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                            MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No Image Available",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
