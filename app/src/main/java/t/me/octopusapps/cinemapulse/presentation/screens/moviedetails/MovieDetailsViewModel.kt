package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToDomain
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiModel
import t.me.octopusapps.domain.usecases.GetMovieDetailsUseCase
import t.me.octopusapps.domain.usecases.IsMovieFavoriteUseCase
import t.me.octopusapps.domain.usecases.SetMovieFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
internal class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<MovieDetailsUiState> =
        MutableStateFlow(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            try {
                val movieDetails =
                    getMovieDetailsUseCase.invoke(movieId).mapToMovieUiModel()
                val isFavorite = isMovieFavoriteUseCase(movieId)
                _uiState.value = MovieDetailsUiState.Success(movieDetails, isFavorite)
            } catch (e: Exception) {
                _uiState.value =
                    MovieDetailsUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun onFavoriteClick() {
        val state = _uiState.value as? MovieDetailsUiState.Success ?: return
        if (state.isFavoriteUpdating) return

        val newFavoriteState = !state.isFavorite
        viewModelScope.launch {
            _uiState.value = state.copy(isFavoriteUpdating = true)
            try {
                setMovieFavoriteUseCase(state.movie.mapToDomain(), newFavoriteState)
                _uiState.value = state.copy(
                    isFavorite = newFavoriteState,
                    isFavoriteUpdating = false
                )
            } catch (_: Exception) {
                _uiState.value = state.copy(isFavoriteUpdating = false)
            }
        }
    }
}
