package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import t.me.octopusapps.cinemapulse.presentation.errors.toMovieErrorMessage
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToDomain
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiModel
import t.me.octopusapps.domain.usecases.GetMovieDetailsUseCase
import t.me.octopusapps.domain.usecases.IsMovieFavoriteUseCase
import t.me.octopusapps.domain.usecases.IsMovieWatchedUseCase
import t.me.octopusapps.domain.usecases.SetMovieFavoriteUseCase
import t.me.octopusapps.domain.usecases.SetMovieWatchedUseCase

@HiltViewModel
internal class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val isMovieWatchedUseCase: IsMovieWatchedUseCase,
    private val setMovieWatchedUseCase: SetMovieWatchedUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<MovieDetailsUiState> =
        MutableStateFlow(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState

    private var detailsJob: Job? = null
    private var favoriteJob: Job? = null
    private var watchedJob: Job? = null
    private var detailsRequestId = 0L

    fun fetchMovieDetails(movieId: Int) {
        detailsJob?.cancel()
        favoriteJob?.cancel()
        watchedJob?.cancel()

        val requestId = ++detailsRequestId
        detailsJob = viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            try {
                val movieDetails =
                    getMovieDetailsUseCase.invoke(movieId).mapToMovieUiModel()
                val isFavorite = isMovieFavoriteUseCase(movieId)
                val isWatched = isMovieWatchedUseCase(movieId)
                if (requestId == detailsRequestId) {
                    _uiState.value = MovieDetailsUiState.Success(
                        movie = movieDetails,
                        isFavorite = isFavorite,
                        isWatched = isWatched,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (requestId == detailsRequestId) {
                    _uiState.value =
                        MovieDetailsUiState.Error(e.toMovieErrorMessage())
                }
            }
        }
    }

    fun onFavoriteClick() {
        val state = _uiState.value as? MovieDetailsUiState.Success ?: return
        if (state.isFavoriteUpdating) return

        val movie = state.movie
        val newFavoriteState = !state.isFavorite
        updateMovieState(movie.id) {
            it.copy(isFavoriteUpdating = true)
        }

        favoriteJob = viewModelScope.launch {
            try {
                setMovieFavoriteUseCase(movie.mapToDomain(), newFavoriteState)
                updateMovieState(movie.id) {
                    it.copy(
                        isFavorite = newFavoriteState,
                        isFavoriteUpdating = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                updateMovieState(movie.id) {
                    it.copy(isFavoriteUpdating = false)
                }
            }
        }
    }

    fun onWatchedClick() {
        val state = _uiState.value as? MovieDetailsUiState.Success ?: return
        if (state.isWatchedUpdating) return

        val movie = state.movie
        val newWatchedState = !state.isWatched
        updateMovieState(movie.id) {
            it.copy(isWatchedUpdating = true)
        }

        watchedJob = viewModelScope.launch {
            try {
                setMovieWatchedUseCase(movie.mapToDomain(), newWatchedState)
                updateMovieState(movie.id) {
                    it.copy(
                        isWatched = newWatchedState,
                        isWatchedUpdating = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                updateMovieState(movie.id) {
                    it.copy(isWatchedUpdating = false)
                }
            }
        }
    }

    private fun updateMovieState(
        movieId: Int,
        transform: (MovieDetailsUiState.Success) -> MovieDetailsUiState.Success,
    ) {
        _uiState.update { state ->
            if (state is MovieDetailsUiState.Success && state.movie.id == movieId) {
                transform(state)
            } else {
                state
            }
        }
    }
}
