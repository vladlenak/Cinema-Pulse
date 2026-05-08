package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.usecases.GetMoviesByCategoryUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetMoviesByCategoryUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { useCase(any(), any()) } returns fakeMovieList(page = 1)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial selected category is popular`() {
        val viewModel = MovieListViewModel(useCase)

        assertEquals(MovieCategory.POPULAR, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `onCategorySelected updates selected category`() = runTest {
        val viewModel = MovieListViewModel(useCase)

        viewModel.onCategorySelected(MovieCategory.TOP_RATED)
        advanceUntilIdle()

        assertEquals(MovieCategory.TOP_RATED, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `onCategorySelected ignores current category`() = runTest {
        val viewModel = MovieListViewModel(useCase)

        viewModel.onCategorySelected(MovieCategory.POPULAR)
        advanceUntilIdle()

        assertEquals(MovieCategory.POPULAR, viewModel.uiState.value.selectedCategory)
    }
}

class MovieCategoryPagingSourceTest {

    private val useCase: GetMoviesByCategoryUseCase = mockk()

    @Test
    fun `load returns first page with next key`() = runTest {
        coEvery {
            useCase(MovieCategory.POPULAR, 1)
        } returns fakeMovieList(page = 1, totalPages = 3)
        val pagingSource = MovieCategoryPagingSource(MovieCategory.POPULAR, useCase)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        val page = result as PagingSource.LoadResult.Page
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
        assertEquals("Movie 1", page.data.single().title)
        coVerify { useCase(MovieCategory.POPULAR, 1) }
    }

    @Test
    fun `load returns last page without next key`() = runTest {
        coEvery {
            useCase(MovieCategory.TOP_RATED, 3)
        } returns fakeMovieList(page = 3, totalPages = 3)
        val pagingSource = MovieCategoryPagingSource(MovieCategory.TOP_RATED, useCase)

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 3,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals(2, page.prevKey)
        assertNull(page.nextKey)
        assertEquals("Movie 3", page.data.single().title)
    }

    @Test
    fun `load returns error when use case fails`() = runTest {
        val error = IOException("Network error")
        coEvery { useCase(any(), any()) } throws error
        val pagingSource = MovieCategoryPagingSource(MovieCategory.POPULAR, useCase)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(error, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `getRefreshKey returns closest loaded page key`() {
        val pagingSource = MovieCategoryPagingSource(MovieCategory.POPULAR, useCase)
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(fakeMovie(id = 20, title = "Movie 2")),
                    prevKey = 1,
                    nextKey = 3,
                ),
            ),
            anchorPosition = 0,
            config = androidx.paging.PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(2, refreshKey)
    }
}

private fun fakeMovieList(page: Int, totalPages: Int = 5) = MovieList(
    page = page,
    totalPages = totalPages,
    results = listOf(fakeMovie(id = page * 10, title = "Movie $page")),
)

private fun fakeMovie(id: Int, title: String) = Movie(
    id = id,
    title = title,
    overview = "Overview",
    popularity = 9.0,
    releaseDate = "2024-01-01",
    voteAverage = 8.0,
    voteCount = 1000,
    posterPath = "/poster.jpg",
    backdropPath = null,
    genreIds = listOf(28),
    adult = false,
    originalLanguage = "en",
    originalTitle = title,
    video = false,
)
