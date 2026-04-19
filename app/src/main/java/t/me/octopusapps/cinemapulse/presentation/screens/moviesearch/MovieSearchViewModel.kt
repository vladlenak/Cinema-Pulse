package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase
import javax.inject.Inject

@OptIn(FlowPreview::class)
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
            .debounce(400L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                _uiState.value = MovieSearchUiState.Loading
                try {
                    val movies = searchMoviesUseCase(query).mapToMovieUiList().results
                    _uiState.value = MovieSearchUiState.Success(movies)
                } catch (e: Exception) {
                    _uiState.value = MovieSearchUiState.Error(e.message ?: "Unknown Error")
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _query.value = query
        if (query.isBlank()) {
            _uiState.value = MovieSearchUiState.Success(emptyList())
        }
    }
}