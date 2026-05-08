package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import t.me.octopusapps.cinemapulse.R
import t.me.octopusapps.cinemapulse.presentation.components.MoviePosterCard
import t.me.octopusapps.cinemapulse.presentation.components.StateMessageComponent
import t.me.octopusapps.cinemapulse.presentation.config.labelRes
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.text.asString
import t.me.octopusapps.domain.models.MovieCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()
    val categories = MovieCategory.entries

    LaunchedEffect(uiState.selectedCategory) {
        gridState.scrollToItem(0)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.app_name))
                        Text(
                            text = stringResource(uiState.selectedCategory.labelRes),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = categories.indexOf(uiState.selectedCategory),
                edgePadding = 16.dp,
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        text = { Text(stringResource(category.labelRes)) },
                    )
                }
            }

            when {
                movies.loadState.refresh is LoadState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                movies.loadState.refresh is LoadState.Error && movies.itemCount == 0 -> {
                    val error = movies.loadState.refresh as LoadState.Error
                    StateMessageComponent(
                        modifier = Modifier.fillMaxSize(),
                        icon = Icons.Default.Warning,
                        title = stringResource(R.string.movie_list_load_error_title),
                        message = error.error.toMovieErrorMessage().asString(),
                        actionLabel = stringResource(R.string.common_try_again),
                        onActionClick = movies::retry,
                        isError = true,
                    )
                }

                movies.itemCount == 0 -> {
                    StateMessageComponent(
                        modifier = Modifier.fillMaxSize(),
                        icon = Icons.Default.Search,
                        title = stringResource(R.string.movie_list_empty_title),
                        message = stringResource(R.string.movie_list_empty_message),
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 148.dp),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        items(
                            count = movies.itemCount,
                            key = movies.itemKey { movie -> movie.id },
                        ) { index ->
                            val movie = movies[index]
                            if (movie != null) {
                                MoviePosterCard(
                                    movie = movie,
                                    modifier = Modifier.animateItem(),
                                ) {
                                    onMovieClick(movie.id)
                                }
                            }
                        }

                        if (movies.loadState.append is LoadState.Loading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (movies.loadState.append is LoadState.Error) {
                            val error = movies.loadState.append as LoadState.Error
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                StateMessageComponent(
                                    modifier = Modifier
                                        .animateItem()
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    icon = Icons.Default.Warning,
                                    title = stringResource(
                                        R.string.movie_list_load_more_error_title,
                                    ),
                                    message = error.error.toMovieErrorMessage().asString(),
                                    actionLabel = stringResource(R.string.common_retry),
                                    onActionClick = movies::retry,
                                    isError = true,
                                    compact = true,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
