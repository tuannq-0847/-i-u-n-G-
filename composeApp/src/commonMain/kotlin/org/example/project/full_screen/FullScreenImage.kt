package org.example.project.full_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.PlayerSpeed
import chaintech.videoplayer.model.ScreenResize
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.headers
import org.example.project.DotsIndicator
import org.example.project.data.LocationInfo
import org.example.project.extension.isVideo

@Composable
fun FullScreenImages(
    locationInfo: LocationInfo,
    onClick: ((Int) -> Unit)? = null,
) {
    Column {
        if (locationInfo.imageUrlList.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { locationInfo.imageUrlList.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth().height(400.dp),
                contentPadding = PaddingValues(horizontal = 12.dp), // creates the visible side peek
            ) { pageIndex ->
                val url = locationInfo.imageUrlList[pageIndex]
                if (url.isVideo()) {
                    val playerHost = MediaPlayerHost(mediaUrl = url)

                    VideoPlayerComposable(
                        modifier = Modifier.fillMaxSize().clickable {
                            onClick?.invoke(pageIndex)
                        },
                        playerHost = playerHost
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(locationInfo.imageUrlList[pageIndex])
                            .build(),
                        contentDescription = "${locationInfo.name} image ${pageIndex + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth().padding(horizontal = 2.dp)
                            .clip(MaterialTheme.shapes.medium) // Clip if Pager has padding or rounded corners
                            .background(MaterialTheme.colorScheme.outlineVariant)
                            .clickable {
                                onClick?.invoke(pageIndex)
                            }
                    )
                }
            }

            // Dots Indicator
            if (pagerState.pageCount > 1) { // Show indicator only if there's more than one image
                DotsIndicator(
                    pageCount = pagerState.pageCount,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Space after images/indicator

        } else {
            // Placeholder for no image (same as before)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image Available", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
