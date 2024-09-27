package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import t.me.octopusapps.cinemapulse.BuildConfig
import t.me.octopusapps.cinemapulse.presentation.components.MovieItemComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPopularMovies(BuildConfig.API_KEY)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Popular Movies") },
                actions = {
                    IconButton(onClick = { onSearchClick() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search Movies")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState.value) {
            is MovieListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }

            is MovieListUiState.Success -> {
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(state.movies) { movie ->
                        MovieItemComponent(movie) {
                            onMovieClick(movie.id)
                        }
                    }
                }
            }

            is MovieListUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(innerPadding),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}