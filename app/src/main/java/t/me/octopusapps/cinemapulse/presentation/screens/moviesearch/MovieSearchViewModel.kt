package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase

@OptIn(FlowPreview::class)
@HiltViewModel
internal class MovieSearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieSearchUiState())
    val uiState = _uiState.asStateFlow()

    private val queryState = MutableStateFlow("")
    private val retryRequests = MutableStateFlow(0)

    init {
        observeSearchRequests()
    }

    fun onQueryChanged(query: String) {
        if (_uiState.value.query == query) return
        _uiState.value = MovieSearchUiState(query = query)
        queryState.value = query
    }

    fun retry() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        _uiState.update { state ->
            state.copy(
                movies = emptyList(),
                error = null,
                isLoading = false,
            )
        }
        retryRequests.update { it + 1 }
    }

    fun clearQuery() {
        if (_uiState.value.query.isNotEmpty()) {
            _uiState.value = MovieSearchUiState()
            queryState.value = ""
        }
    }

    private fun observeSearchRequests() {
        val queryRequests = queryState
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }

        val retrySearchRequests = retryRequests
            .filter { it > 0 }
            .map { queryState.value }
            .filter { it.isNotBlank() }

        viewModelScope.launch {
            merge(queryRequests, retrySearchRequests).collectLatest { query ->
                search(query)
            }
        }
    }

    private suspend fun search(query: String) {
        val trimmedQuery = query.trim()

        _uiState.update { state ->
            if (state.query == query) {
                state.copy(
                    isLoading = true,
                    error = null,
                )
            } else {
                state
            }
        }

        try {
            val movies = searchMoviesUseCase(trimmedQuery).mapToMovieUiList().results
            _uiState.update { state ->
                if (state.query == query) {
                    state.copy(
                        movies = movies,
                        isLoading = false,
                        error = null,
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
                        error = e.toMovieErrorMessage(),
                    )
                } else {
                    state
                }
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
