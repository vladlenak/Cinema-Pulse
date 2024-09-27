package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import t.me.octopusapps.cinemapulse.BuildConfig
import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.domain.constants.ApiConstant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId, BuildConfig.API_KEY)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Movie Details") })
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is MovieDetailsUiState.Loading -> {
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

            is MovieDetailsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: ${state.message}", color = Color.Red)
                }
            }

            is MovieDetailsUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = "${ApiConstant.IMAGE_BASE_URL}${state.movie.posterPath}"
                            ),
                            contentDescription = "${state.movie.title} poster",
                            modifier = Modifier
                                .size(width = 400.dp, height = 600.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = state.movie.title, style = MaterialTheme.typography.headlineMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = state.movie.overview, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    MovieAdditionalInfo(movie = state.movie)

                    Spacer(modifier = Modifier.height(16.dp))

                    state.movie.genreIds?.let { genreIds ->
                        Text(
                            text = "Genres: ${genreIds.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieAdditionalInfo(movie: MovieUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Popularity",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = movie.popularity.toString(),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Language",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = movie.originalLanguage.uppercase(),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Rating",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = movie.voteAverage.toString(),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Release Date",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = movie.releaseDate,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Vote Count",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = movie.voteCount.toString(),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Adult",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = if (movie.adult) "Yes" else "No",
                style = MaterialTheme.typography.bodyLarge,
                color = if (movie.adult) Color.Red else Color.Green
            )
        }
    }
}