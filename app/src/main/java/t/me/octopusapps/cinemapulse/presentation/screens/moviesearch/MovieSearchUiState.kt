package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel

internal data class MovieSearchUiState(
    val query: String = "",
    val movies: List<MovieUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
