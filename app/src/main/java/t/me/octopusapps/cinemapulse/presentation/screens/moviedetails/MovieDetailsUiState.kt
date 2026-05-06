package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.cinemapulse.presentation.text.UiText

internal sealed class MovieDetailsUiState {
    data object Loading : MovieDetailsUiState()
    data class Success(
        val movie: MovieUiModel,
        val isFavorite: Boolean,
        val isFavoriteUpdating: Boolean = false,
        val isWatched: Boolean = false,
        val isWatchedUpdating: Boolean = false
    ) : MovieDetailsUiState()
    data class Error(val message: UiText) : MovieDetailsUiState()
}
