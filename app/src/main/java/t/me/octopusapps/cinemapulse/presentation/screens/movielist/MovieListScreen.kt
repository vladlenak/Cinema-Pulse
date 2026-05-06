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
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import t.me.octopusapps.cinemapulse.presentation.components.MoviePosterCard
import t.me.octopusapps.cinemapulse.presentation.components.StateMessageComponent
import t.me.octopusapps.domain.models.MovieCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val categories = MovieCategory.entries

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 6
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState.canLoadMore) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Cinema Pulse")
                        Text(
                            text = uiState.selectedCategory.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            PrimaryScrollableTabRow(
                selectedTabIndex = categories.indexOf(uiState.selectedCategory),
                edgePadding = 16.dp
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        text = { Text(category.label) }
                    )
                }
            }

            when {
                uiState.isInitialLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null && uiState.movies.isEmpty() -> {
                    StateMessageComponent(
                        modifier = Modifier.fillMaxSize(),
                        icon = Icons.Default.Warning,
                        title = "Could not load movies",
                        message = uiState.error!!,
                        actionLabel = "Try again",
                        onActionClick = viewModel::retry,
                        isError = true
                    )
                }

                uiState.movies.isEmpty() -> {
                    StateMessageComponent(
                        modifier = Modifier.fillMaxSize(),
                        icon = Icons.Default.Search,
                        title = "No movies found",
                        message = "Try another category or check back later."
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
                            bottom = 24.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(
                            items = uiState.movies,
                            key = { movie -> movie.id }
                        ) { movie ->
                            MoviePosterCard(movie) {
                                onMovieClick(movie.id)
                            }
                        }

                        if (uiState.isLoadingMore) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (uiState.error != null && uiState.movies.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                StateMessageComponent(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    icon = Icons.Default.Warning,
                                    title = "Could not load more",
                                    message = uiState.error!!,
                                    actionLabel = "Retry",
                                    onActionClick = viewModel::retry,
                                    isError = true,
                                    compact = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
