package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel

internal sealed class MovieListUiState {
    data object Loading : MovieListUiState()
    data class Success(val movies: List<MovieUiModel>) : MovieListUiState()
    data class Error(val message: String) : MovieListUiState()
}