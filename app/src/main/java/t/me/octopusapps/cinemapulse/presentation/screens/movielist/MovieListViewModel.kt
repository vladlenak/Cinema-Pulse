package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.usecases.GetMoviesByCategoryUseCase
import javax.inject.Inject

@HiltViewModel
internal class MovieListViewModel @Inject constructor(
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNextPage()
    }

    fun onCategorySelected(category: MovieCategory) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.value = MovieListUiState(selectedCategory = category)
        loadNextPage()
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.canLoadMore && state.currentPage > 0) return

        val nextPage = state.currentPage + 1
        val category = state.selectedCategory

        viewModelScope.launch {
            _uiState.update {
                if (it.currentPage == 0) it.copy(isInitialLoading = true)
                else it.copy(isLoadingMore = true)
            }
            try {
                val result = getMoviesByCategoryUseCase(category, nextPage).mapToMovieUiList()
                _uiState.update {
                    it.copy(
                        movies = it.movies + result.results,
                        currentPage = nextPage,
                        totalPages = result.totalPages,
                        isInitialLoading = false,
                        isLoadingMore = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isLoadingMore = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun retry() {
        _uiState.update { it.copy(error = null) }
        loadNextPage()
    }
}