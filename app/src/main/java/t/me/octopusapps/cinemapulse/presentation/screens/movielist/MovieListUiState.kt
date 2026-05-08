package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import t.me.octopusapps.domain.models.MovieCategory

internal data class MovieListUiState(val selectedCategory: MovieCategory = MovieCategory.POPULAR)
