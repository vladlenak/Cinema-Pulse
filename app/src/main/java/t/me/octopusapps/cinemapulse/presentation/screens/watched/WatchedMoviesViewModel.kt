package t.me.octopusapps.cinemapulse.presentation.screens.watched

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiModel
import t.me.octopusapps.domain.usecases.GetWatchedMoviesUseCase

@HiltViewModel
internal class WatchedMoviesViewModel @Inject constructor(
    private val getWatchedMoviesUseCase: GetWatchedMoviesUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<WatchedMoviesUiState> =
        MutableStateFlow(WatchedMoviesUiState.Loading)
    val uiState: StateFlow<WatchedMoviesUiState> = _uiState
    private var watchedJob: Job? = null

    init {
        loadWatched()
    }

    fun loadWatched() {
        watchedJob?.cancel()
        watchedJob = viewModelScope.launch {
            _uiState.value = WatchedMoviesUiState.Loading
            try {
                getWatchedMoviesUseCase().collect { movies ->
                    _uiState.value = WatchedMoviesUiState.Success(
                        movies.map { it.mapToMovieUiModel() },
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value =
                    WatchedMoviesUiState.Error(e.toMovieErrorMessage())
            }
        }
    }
}
