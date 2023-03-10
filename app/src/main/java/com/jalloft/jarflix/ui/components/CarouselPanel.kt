package com.jalloft.jarflix.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.*
import com.jalloft.jarflix.model.movie.Movie
import com.jalloft.jarflix.model.movie.MovieResult
import com.jalloft.jarflix.ui.theme.CarmineRed
import com.jalloft.jarflix.ui.theme.White
import com.jalloft.jarflix.ui.viewmodel.RemoteCallState
import com.jalloft.jarflix.utils.toImageOrigial
import com.jalloft.jarflix.utils.toImageW500
import kotlinx.coroutines.delay
import timber.log.Timber


@Composable
fun CarouselTrendingPanel(remoteCallState: RemoteCallState?, onItemSelected: (MovieResult) -> Unit) {
    when (remoteCallState) {
        is RemoteCallState.Loading -> {
            Timber.i("Carregando...")
            LoadingPanel()
        }
        is RemoteCallState.Success<*> -> {
            remoteCallState.media?.let { trendingResult ->
                CarouselTrending(trendingResult as Movie){onItemSelected(it)}
            }
        }
        else -> {
            Timber.i("Ocorreu um erro")
        }
    }

}

@Composable
fun LoadingPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp)),
            color = White.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CarouselTrending(trendingResult: Movie, onItemSelected: (MovieResult) -> Unit) {
    val trendingList = trendingResult.results

    val pagerState = rememberPagerState()
    Box(
        modifier = Modifier.fillMaxWidth().clickable { onItemSelected(trendingList[pagerState.currentPage]) },
        contentAlignment = Alignment.BottomCenter,
    ) {
        val pageCount = 5
        HorizontalPager(
            count = pageCount.coerceAtMost(trendingList.size),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 18.dp),
            state = pagerState,
        ) { page ->
            val trendingMovie = trendingList[page]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                AsyncImage(
                    model = trendingMovie.backdropPath?.toImageW500,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .blur(8.dp),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = trendingMovie.posterPath?.toImageOrigial,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp, 180.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    trendingMovie.title?.let { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                start = 8.dp,
                                end = 4.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            )
                        )
                    }
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(bottom = 8.dp),
            activeColor = CarmineRed
        )

        val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
        var autoScrollState by remember { mutableStateOf(false) }

        if (!isDragged) {
            LaunchedEffect(autoScrollState) {
                delay(2000)
                with(pagerState) {
                    val page = if (currentPage < pageCount - 1) currentPage.plus(1) else 0
                    animateScrollToPage(page)
                    autoScrollState = !autoScrollState
                }
            }
        }
    }
}
