package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel

sealed class MovieDetailsUiState {
    data object Loading : MovieDetailsUiState()
    data class Success(val movie: MovieUiModel) : MovieDetailsUiState()
    data class Error(val message: String) : MovieDetailsUiState()
}