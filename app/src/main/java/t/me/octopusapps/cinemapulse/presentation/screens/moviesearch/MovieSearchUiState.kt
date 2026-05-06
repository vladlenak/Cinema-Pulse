package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.cinemapulse.presentation.text.UiText

internal data class MovieSearchUiState(
    val query: String = "",
    val movies: List<MovieUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)
