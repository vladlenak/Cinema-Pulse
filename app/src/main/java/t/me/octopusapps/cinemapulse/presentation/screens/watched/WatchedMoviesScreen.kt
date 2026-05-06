package t.me.octopusapps.cinemapulse.presentation.screens.watched

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import t.me.octopusapps.cinemapulse.R
import t.me.octopusapps.cinemapulse.presentation.components.MovieItemComponent
import t.me.octopusapps.cinemapulse.presentation.components.StateMessageComponent
import t.me.octopusapps.cinemapulse.presentation.text.asString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchedMoviesScreen(
    onMovieClick: (Int) -> Unit,
    onBrowseMoviesClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    viewModel: WatchedMoviesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadWatched()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.watched_movies_title)) },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is WatchedMoviesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is WatchedMoviesUiState.Success -> {
                if (state.movies.isEmpty()) {
                    StateMessageComponent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        icon = Icons.Default.VisibilityOff,
                        title = stringResource(R.string.watched_movies_empty_title),
                        message = stringResource(R.string.watched_movies_empty_message),
                        actionLabel = stringResource(R.string.common_browse_movies),
                        onActionClick = onBrowseMoviesClick,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                    ) {
                        items(
                            items = state.movies,
                            key = { movie -> movie.id },
                        ) { movie ->
                            MovieItemComponent(
                                movie = movie,
                                modifier = Modifier.animateItem(),
                            ) {
                                onMovieClick(movie.id)
                            }
                        }
                    }
                }
            }

            is WatchedMoviesUiState.Error -> {
                StateMessageComponent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    icon = Icons.Default.Warning,
                    title = stringResource(R.string.watched_movies_load_error_title),
                    message = state.message.asString(),
                    actionLabel = stringResource(R.string.common_try_again),
                    onActionClick = viewModel::loadWatched,
                    isError = true,
                )
            }
        }
    }
}
