package t.me.octopusapps.cinemapulse.presentation.screens.movielist

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
    private lateinit var viewModel: MovieListViewModel

    private fun fakeMovieList(page: Int, totalPages: Int = 5) = MovieList(
        page = page,
        totalPages = totalPages,
        results = listOf(
            Movie(
                id = page * 10,
                title = "Movie $page",
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
                originalTitle = "Movie $page",
                video = false
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { useCase(any(), any()) } returns fakeMovieList(1)
        viewModel = MovieListViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() {
        assertTrue(viewModel.uiState.value.isInitialLoading)
        assertTrue(viewModel.uiState.value.movies.isEmpty())
    }

    @Test
    fun `initial selected category is popular`() {
        assertEquals(MovieCategory.POPULAR, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `loads first page on init`() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isInitialLoading)
        assertEquals(1, state.movies.size)
        assertEquals(1, state.currentPage)
        assertNull(state.error)
    }

    @Test
    fun `loadNextPage appends movies to existing list`() = runTest {
        advanceUntilIdle()

        coEvery { useCase(any(), 2) } returns fakeMovieList(2)
        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.movies.size)
        assertEquals(2, viewModel.uiState.value.currentPage)
    }

    @Test
    fun `loadNextPage does nothing when already on last page`() = runTest {
        coEvery { useCase(any(), 1) } returns fakeMovieList(page = 1, totalPages = 1)
        val vm = MovieListViewModel(useCase)
        advanceUntilIdle()

        vm.loadNextPage()
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.currentPage)
        assertFalse(vm.uiState.value.canLoadMore)
    }

    @Test
    fun `onCategorySelected resets state and loads new category`() = runTest {
        advanceUntilIdle()

        coEvery { useCase(MovieCategory.TOP_RATED, 1) } returns fakeMovieList(1)
        viewModel.onCategorySelected(MovieCategory.TOP_RATED)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(MovieCategory.TOP_RATED, state.selectedCategory)
        assertEquals(1, state.movies.size)
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `onCategorySelected does nothing when same category selected`() = runTest {
        advanceUntilIdle()
        val moviesBefore = viewModel.uiState.value.movies

        viewModel.onCategorySelected(MovieCategory.POPULAR)
        advanceUntilIdle()

        assertEquals(moviesBefore, viewModel.uiState.value.movies)
    }

    @Test
    fun `shows error when use case throws`() = runTest {
        coEvery { useCase(any(), any()) } throws Exception("Network error")
        viewModel = MovieListViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertEquals("Network error", state.error)
        assertFalse(state.isInitialLoading)
    }

    @Test
    fun `retry clears error and reloads`() = runTest {
        coEvery { useCase(any(), any()) } throws Exception("Network error")
        viewModel = MovieListViewModel(useCase)
        advanceUntilIdle()

        coEvery { useCase(any(), any()) } returns fakeMovieList(1)
        viewModel.retry()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertEquals(1, viewModel.uiState.value.movies.size)
    }

    @Test
    fun `canLoadMore is false when on last page`() = runTest {
        coEvery { useCase(any(), 1) } returns fakeMovieList(page = 1, totalPages = 1)
        val vm = MovieListViewModel(useCase)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.canLoadMore)
    }

    @Test
    fun `canLoadMore is true when more pages available`() = runTest {
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canLoadMore)
    }
}