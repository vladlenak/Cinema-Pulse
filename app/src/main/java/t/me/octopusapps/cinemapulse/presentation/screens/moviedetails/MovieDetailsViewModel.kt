package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiModel
import t.me.octopusapps.domain.usecases.GetMovieDetailsUseCase
import javax.inject.Inject

@HiltViewModel
internal class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<MovieDetailsUiState> =
        MutableStateFlow(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState

    fun fetchMovieDetails(movieId: Int, apiKey: String) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            try {
                val movieDetails =
                    getMovieDetailsUseCase.invoke(movieId, apiKey).mapToMovieUiModel()
                _uiState.value = MovieDetailsUiState.Success(movieDetails)
            } catch (e: Exception) {
                _uiState.value =
                    MovieDetailsUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

}