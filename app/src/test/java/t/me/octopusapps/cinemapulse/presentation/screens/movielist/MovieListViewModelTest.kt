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
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.usecases.GetPopularMoviesUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetPopularMoviesUseCase = mockk()
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
        coEvery { useCase(any()) } returns fakeMovieList(1)
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

        coEvery { useCase(2) } returns fakeMovieList(2)
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.movies.size)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `loadNextPage does nothing when already on last page`() = runTest {
        coEvery { useCase(1) } returns fakeMovieList(page = 1, totalPages = 1)
        val vm = MovieListViewModel(useCase)
        advanceUntilIdle()

        vm.loadNextPage()
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.currentPage)
        assertFalse(vm.uiState.value.canLoadMore)
    }

    @Test
    fun `shows error when use case throws`() = runTest {
        coEvery { useCase(any()) } throws Exception("Network error")
        viewModel = MovieListViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertEquals("Network error", state.error)
        assertFalse(state.isInitialLoading)
    }

    @Test
    fun `retry clears error and reloads`() = runTest {
        coEvery { useCase(any()) } throws Exception("Network error")
        viewModel = MovieListViewModel(useCase)
        advanceUntilIdle()

        coEvery { useCase(any()) } returns fakeMovieList(1)
        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(1, state.movies.size)
    }

    @Test
    fun `canLoadMore is false when on last page`() = runTest {
        coEvery { useCase(1) } returns fakeMovieList(page = 1, totalPages = 1)
        val vm = MovieListViewModel(useCase)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.canLoadMore)
    }

    @Test
    fun `canLoadMore is true when more pages available`() = runTest {
        coEvery { useCase(1) } returns fakeMovieList(page = 1, totalPages = 5)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canLoadMore)
    }
}