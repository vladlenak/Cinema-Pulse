package t.me.octopusapps.cinemapulse.presentation.screens.favorites

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.cinemapulse.presentation.text.UiText

internal sealed class FavoriteMoviesUiState {
    data object Loading : FavoriteMoviesUiState()
    data class Success(val movies: List<MovieUiModel>) : FavoriteMoviesUiState()
    data class Error(val message: UiText) : FavoriteMoviesUiState()
}
