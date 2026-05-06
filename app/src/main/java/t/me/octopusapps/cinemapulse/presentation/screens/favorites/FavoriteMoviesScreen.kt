package t.me.octopusapps.cinemapulse.presentation.screens.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import t.me.octopusapps.cinemapulse.presentation.components.MovieItemComponent
import t.me.octopusapps.cinemapulse.presentation.components.StateMessageComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteMoviesScreen(
    onMovieClick: (Int) -> Unit,
    onBrowseMoviesClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    viewModel: FavoriteMoviesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadFavorites()
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
                title = { Text("Favorites") },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is FavoriteMoviesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is FavoriteMoviesUiState.Success -> {
                if (state.movies.isEmpty()) {
                    StateMessageComponent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        icon = Icons.Default.FavoriteBorder,
                        title = "No favorites yet",
                        message = "Open a movie and tap the heart to build your watchlist.",
                        actionLabel = "Browse movies",
                        onActionClick = onBrowseMoviesClick
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(state.movies) { movie ->
                            MovieItemComponent(movie) {
                                onMovieClick(movie.id)
                            }
                        }
                    }
                }
            }

            is FavoriteMoviesUiState.Error -> {
                StateMessageComponent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    icon = Icons.Default.Warning,
                    title = "Could not load favorites",
                    message = state.message,
                    actionLabel = "Try again",
                    onActionClick = viewModel::loadFavorites,
                    isError = true
                )
            }
        }
    }
}
