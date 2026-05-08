package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.usecases.GetMoviesByCategoryUseCase

internal class MovieCategoryPagingSource(
    private val category: MovieCategory,
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: INITIAL_PAGE

        return try {
            val movies = getMoviesByCategoryUseCase(category, page)
            LoadResult.Page(
                data = movies.results,
                prevKey = if (page == INITIAL_PAGE) null else page - 1,
                nextKey = if (page >= movies.totalPages) null else page + 1,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = state.anchorPosition
        ?.let { anchorPosition -> state.closestPageToPosition(anchorPosition) }
        ?.let { anchorPage -> anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1) }

    private companion object {
        const val INITIAL_PAGE = 1
    }
}
