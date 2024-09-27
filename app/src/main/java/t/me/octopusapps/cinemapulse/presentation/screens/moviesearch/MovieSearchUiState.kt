package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel

sealed class MovieSearchUiState {
    data object Loading : MovieSearchUiState()
    data class Success(val movies: List<MovieUiModel>) : MovieSearchUiState()
    data class Error(val message: String) : MovieSearchUiState()
}