package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.usecases.GetMoviesByCategoryUseCase

@HiltViewModel
internal class MovieListViewModel @Inject constructor(
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState = _uiState.asStateFlow()

    private var pageLoadJob: Job? = null

    init {
        loadNextPage()
    }

    fun onCategorySelected(category: MovieCategory) {
        if (_uiState.value.selectedCategory == category) return
        pageLoadJob?.cancel()
        _uiState.value = MovieListUiState(selectedCategory = category)
        loadNextPage()
    }

    fun loadNextPage() {
        if (pageLoadJob?.isActive == true) return

        val state = _uiState.value
        if (!state.canLoadMore && state.currentPage > 0) return

        val nextPage = state.currentPage + 1
        val category = state.selectedCategory

        _uiState.update {
            if (nextPage == 1) {
                it.copy(isInitialLoading = true)
            } else {
                it.copy(isLoadingMore = true)
            }
        }

        pageLoadJob = viewModelScope.launch {
            try {
                val result = getMoviesByCategoryUseCase(category, nextPage).mapToMovieUiList()
                _uiState.update {
                    if (it.selectedCategory == category && it.currentPage < nextPage) {
                        it.copy(
                            movies = it.movies + result.results,
                            currentPage = nextPage,
                            totalPages = result.totalPages,
                            isInitialLoading = false,
                            isLoadingMore = false,
                            error = null,
                        )
                    } else {
                        it
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    if (it.selectedCategory == category) {
                        it.copy(
                            isInitialLoading = false,
                            isLoadingMore = false,
                            error = e.toMovieErrorMessage(),
                        )
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun retry() {
        _uiState.update { it.copy(error = null) }
        loadNextPage()
    }
}
