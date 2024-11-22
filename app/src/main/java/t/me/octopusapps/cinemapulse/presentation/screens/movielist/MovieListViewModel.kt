package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiList
import t.me.octopusapps.domain.usecases.GetPopularMoviesUseCase
import javax.inject.Inject

@HiltViewModel
internal class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<MovieListUiState> =
        MutableStateFlow(MovieListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun fetchPopularMovies(apiKey: String) {
        viewModelScope.launch {
            try {
                val movies = getPopularMoviesUseCase(apiKey).mapToMovieUiList().results
                _uiState.value = MovieListUiState.Success(movies)
            } catch (e: Exception) {
                _uiState.value = MovieListUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

}