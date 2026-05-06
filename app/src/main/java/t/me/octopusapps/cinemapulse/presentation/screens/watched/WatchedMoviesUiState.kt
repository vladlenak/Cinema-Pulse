package t.me.octopusapps.cinemapulse.presentation.screens.watched

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.cinemapulse.presentation.text.UiText

internal sealed class WatchedMoviesUiState {
    data object Loading : WatchedMoviesUiState()
    data class Success(val movies: List<MovieUiModel>) : WatchedMoviesUiState()
    data class Error(val message: UiText) : WatchedMoviesUiState()
}
