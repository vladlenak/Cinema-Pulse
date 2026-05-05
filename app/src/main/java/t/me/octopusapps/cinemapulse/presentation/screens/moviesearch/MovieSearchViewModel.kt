package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class MovieSearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MovieSearchUiState>(MovieSearchUiState.Success(emptyList()))
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")

    init {
        _query
            .mapLatest { query ->
                val trimmedQuery = query.trim()
                if (trimmedQuery.isBlank()) {
                    MovieSearchUiState.Success(emptyList())
                } else {
                    delay(SEARCH_DEBOUNCE_MS)
                    _uiState.value = MovieSearchUiState.Loading
                    try {
                        val movies = searchMoviesUseCase(trimmedQuery).mapToMovieUiList().results
                        MovieSearchUiState.Success(movies)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        MovieSearchUiState.Error(e.toMovieErrorMessage(defaultMessage = "Unknown Error"))
                    }
                }
            }
            .onEach { state -> _uiState.value = state }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
