package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase
import javax.inject.Inject

@HiltViewModel
internal class MovieSearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieSearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        setQuery(query)
        search(query)
    }

    fun retry() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        _uiState.update { state ->
            state.copy(
                movies = emptyList(),
                error = null,
                isLoading = false
            )
        }
        search(query)
    }

    fun clearQuery() {
        if (_uiState.value.query.isNotEmpty()) {
            searchJob?.cancel()
            _uiState.value = MovieSearchUiState()
        }
    }

    private fun setQuery(query: String) {
        _uiState.value = MovieSearchUiState(query = query)
    }

    private fun search(query: String) {
        searchJob?.cancel()

        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            if (_uiState.value.query != query) return@launch

            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val movies = searchMoviesUseCase(trimmedQuery).mapToMovieUiList().results
                _uiState.update { state ->
                    if (state.query == query) {
                        state.copy(
                            movies = movies,
                            isLoading = false,
                            error = null
                        )
                    } else {
                        state
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { state ->
                    if (state.query == query) {
                        state.copy(
                            movies = emptyList(),
                            isLoading = false,
                            error = e.toMovieErrorMessage(defaultMessage = "Unknown Error")
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
