package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import t.me.octopusapps.cinemapulse.BuildConfig
import t.me.octopusapps.cinemapulse.presentation.components.MovieItemComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearchScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: MovieSearchViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search Movies") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = query,
                onValueChange = { newQuery ->
                    query = newQuery
                    if (query.isNotEmpty()) {
                        viewModel.searchMovies(query, BuildConfig.API_KEY)
                    }
                },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is MovieSearchUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                }

                is MovieSearchUiState.Success -> {
                    LazyColumn {
                        items(state.movies) { movie ->
                            MovieItemComponent(movie) {
                                onMovieClick(movie.id)
                            }
                        }
                    }
                }

                is MovieSearchUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}