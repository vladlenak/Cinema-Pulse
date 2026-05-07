package t.me.octopusapps.cinemapulse.presentation.screens.favorites

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
import t.me.octopusapps.domain.usecases.GetFavoriteMoviesUseCase

@HiltViewModel
internal class FavoriteMoviesViewModel @Inject constructor(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<FavoriteMoviesUiState> =
        MutableStateFlow(FavoriteMoviesUiState.Loading)
    val uiState: StateFlow<FavoriteMoviesUiState> = _uiState
    private var favoritesJob: Job? = null

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            _uiState.value = FavoriteMoviesUiState.Loading
            try {
                getFavoriteMoviesUseCase().collect { movies ->
                    _uiState.value = FavoriteMoviesUiState.Success(
                        movies.map { it.mapToMovieUiModel() },
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value =
                    FavoriteMoviesUiState.Error(e.toMovieErrorMessage())
            }
        }
    }
}
