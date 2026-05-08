package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import t.me.octopusapps.cinemapulse.presentation.mapper.mapToMovieUiModel
import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.usecases.GetMoviesByCategoryUseCase

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class MovieListViewModel @Inject constructor(
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase,
) : ViewModel() {

    private val selectedCategory = MutableStateFlow(MovieCategory.POPULAR)
    val uiState = selectedCategory
        .map { category -> MovieListUiState(selectedCategory = category) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MovieListUiState(),
        )

    val movies: Flow<PagingData<MovieUiModel>> = selectedCategory
        .flatMapLatest { category ->
            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    initialLoadSize = PAGE_SIZE,
                    prefetchDistance = PREFETCH_DISTANCE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    MovieCategoryPagingSource(
                        category = category,
                        getMoviesByCategoryUseCase = getMoviesByCategoryUseCase,
                    )
                },
            ).flow
        }
        .map { pagingData ->
            pagingData.map { movie -> movie.mapToMovieUiModel() }
        }
        .cachedIn(viewModelScope)

    fun onCategorySelected(category: MovieCategory) {
        if (selectedCategory.value == category) return
        selectedCategory.value = category
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 6
    }
}
