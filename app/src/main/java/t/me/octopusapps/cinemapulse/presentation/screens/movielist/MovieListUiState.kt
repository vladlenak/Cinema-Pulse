package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.domain.models.MovieCategory

internal data class MovieListUiState(
    val movies: List<MovieUiModel> = emptyList(),
    val isInitialLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 1,
    val selectedCategory: MovieCategory = MovieCategory.POPULAR
) {
    val canLoadMore: Boolean get() = currentPage < totalPages && !isLoadingMore
}