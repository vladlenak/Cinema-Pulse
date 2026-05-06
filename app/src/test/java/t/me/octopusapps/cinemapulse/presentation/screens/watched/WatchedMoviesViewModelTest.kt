package t.me.octopusapps.cinemapulse.presentation.screens.watched

import io.mockk.coEvery
import io.mockk.coVerify
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
        coEvery { useCase() } returns emptyList()

        val viewModel = WatchedMoviesViewModel(useCase)

        assertTrue(viewModel.uiState.value is WatchedMoviesUiState.Loading)
    }

    @Test
    fun `loads watched movies on init`() = runTest {
        coEvery { useCase() } returns listOf(fakeMovie)

        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Success)
        assertEquals(1, (state as WatchedMoviesUiState.Success).movies.size)
        assertEquals("Inception", state.movies[0].title)
    }

    @Test
    fun `shows empty success when there are no watched movies`() = runTest {
        coEvery { useCase() } returns emptyList()

        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Success)
        assertTrue((state as WatchedMoviesUiState.Success).movies.isEmpty())
    }

    @Test
    fun `shows error when use case throws`() = runTest {
        coEvery { useCase() } throws Exception("Storage error")

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
    fun `loadWatched reloads watched movies`() = runTest {
        coEvery { useCase() } returns emptyList()
        val viewModel = WatchedMoviesViewModel(useCase)
        advanceUntilIdle()

        coEvery { useCase() } returns listOf(fakeMovie)
        viewModel.loadWatched()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WatchedMoviesUiState.Success)
        assertEquals(1, (state as WatchedMoviesUiState.Success).movies.size)
        coVerify(exactly = 2) { useCase() }
    }
}
