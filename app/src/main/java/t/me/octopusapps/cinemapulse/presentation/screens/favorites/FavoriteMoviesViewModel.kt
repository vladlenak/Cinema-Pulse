package t.me.octopusapps.cinemapulse.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiModel
import t.me.octopusapps.domain.usecases.GetFavoriteMoviesUseCase
import javax.inject.Inject

@HiltViewModel
internal class FavoriteMoviesViewModel @Inject constructor(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<FavoriteMoviesUiState> =
        MutableStateFlow(FavoriteMoviesUiState.Loading)
    val uiState: StateFlow<FavoriteMoviesUiState> = _uiState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoriteMoviesUiState.Loading
            try {
                val movies = getFavoriteMoviesUseCase().map { it.mapToMovieUiModel() }
                _uiState.value = FavoriteMoviesUiState.Success(movies)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value =
                    FavoriteMoviesUiState.Error(e.toMovieErrorMessage())
            }
        }
    }
}
