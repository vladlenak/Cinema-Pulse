package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<MovieSearchUiState> =
        MutableStateFlow(MovieSearchUiState.Success(movies = emptyList()))
    val uiState = _uiState.asStateFlow()

    fun searchMovies(query: String, apiKey: String) = viewModelScope.launch {
        _uiState.value = MovieSearchUiState.Loading

        try {
            val movies = searchMoviesUseCase(query, apiKey).mapToMovieUiList().results
            _uiState.value = MovieSearchUiState.Success(movies)
        } catch (e: Exception) {
            _uiState.value = MovieSearchUiState.Error(e.message ?: "Unknown Error")
        }
    }

}