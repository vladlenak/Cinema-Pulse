package t.me.octopusapps.cinemapulse.presentation.screens.watched

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiModel
import t.me.octopusapps.domain.usecases.GetWatchedMoviesUseCase
import javax.inject.Inject

@HiltViewModel
internal class WatchedMoviesViewModel @Inject constructor(
    private val getWatchedMoviesUseCase: GetWatchedMoviesUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<WatchedMoviesUiState> =
        MutableStateFlow(WatchedMoviesUiState.Loading)
    val uiState: StateFlow<WatchedMoviesUiState> = _uiState

    init {
        loadWatched()
    }

    fun loadWatched() {
        viewModelScope.launch {
            _uiState.value = WatchedMoviesUiState.Loading
            try {
                val movies = getWatchedMoviesUseCase().map { it.mapToMovieUiModel() }
                _uiState.value = WatchedMoviesUiState.Success(movies)
            } catch (e: Exception) {
                _uiState.value =
                    WatchedMoviesUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}
