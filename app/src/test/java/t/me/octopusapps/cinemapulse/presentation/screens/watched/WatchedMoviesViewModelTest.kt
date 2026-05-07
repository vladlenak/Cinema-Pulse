package t.me.octopusapps.cinemapulse.presentation.screens.watched

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import t.me.octopusapps.cinemapulse.presentation.text.UiText
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.usecases.GetWatchedMoviesUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class WatchedMoviesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetWatchedMoviesUseCase = mockk()

    private val fakeMovie = Movie(
        id = 1,
        title = "Inception",
        overview = "A dream within a dream",
        popularity = 9.5,
        releaseDate = "2010-07-16",
        voteAverage = 8.8,
        voteCount = 30000,
        posterPath = "/poster.jpg",
        backdropPath = null,
        genreIds = listOf(28, 878),
        adult = false,
        originalLanguage = "en",
        originalTitle = "Inception",
        video = false,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() {
        every { useCase() } returns flowOf(emptyList())

        val viewModel = WatchedMoviesViewModel(useCase)

        assertTrue(viewModel.uiState.value is WatchedMoviesUiState.Loading)
    }

    @Test
    fun `loads watched movies on init`() = runTest {
        every { useCase() } returns flowOf(listOf(fakeMovie))

        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Success)
        assertEquals(1, (state as WatchedMoviesUiState.Success).movies.size)
        assertEquals("Inception", state.movies[0].title)
    }

    @Test
    fun `shows empty success when there are no watched movies`() = runTest {
        every { useCase() } returns flowOf(emptyList())

        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Success)
        assertTrue((state as WatchedMoviesUiState.Success).movies.isEmpty())
    }

    @Test
    fun `shows error when use case throws`() = runTest {
        every { useCase() } returns flow {
            throw IllegalStateException("Storage error")
        }

        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Error)
        assertEquals(
            UiText.DynamicString("Storage error"),
            (state as WatchedMoviesUiState.Error).message,
        )
    }

    @Test
    fun `loadWatched resubscribes to watched movies`() = runTest {
        every { useCase() } returns flowOf(emptyList())
        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        every { useCase() } returns flowOf(listOf(fakeMovie))
        viewModel.loadWatched()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Success)
        assertEquals(1, (state as WatchedMoviesUiState.Success).movies.size)
        verify(exactly = 2) { useCase() }
    }

    @Test
    fun `updates watched movies when flow emits new list`() = runTest {
        val watchedMovies = MutableStateFlow(emptyList<Movie>())
        every { useCase() } returns watchedMovies
        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        watchedMovies.value = listOf(fakeMovie)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Success)
        assertEquals(1, (state as WatchedMoviesUiState.Success).movies.size)
        assertEquals("Inception", state.movies[0].title)
    }
}
