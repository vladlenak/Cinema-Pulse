package t.me.octopusapps.cinemapulse.presentation.screens.favorites

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
import t.me.octopusapps.domain.usecases.GetFavoriteMoviesUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteMoviesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetFavoriteMoviesUseCase = mockk()

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

        val viewModel = FavoriteMoviesViewModel(useCase)

        assertTrue(viewModel.uiState.value is FavoriteMoviesUiState.Loading)
    }

    @Test
    fun `loads favorites on init`() = runTest {
        every { useCase() } returns flowOf(listOf(fakeMovie))

        val viewModel = FavoriteMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FavoriteMoviesUiState.Success)
        assertEquals(1, (state as FavoriteMoviesUiState.Success).movies.size)
        assertEquals("Inception", state.movies[0].title)
    }

    @Test
    fun `shows empty success when there are no favorites`() = runTest {
        every { useCase() } returns flowOf(emptyList())

        val viewModel = FavoriteMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FavoriteMoviesUiState.Success)
        assertTrue((state as FavoriteMoviesUiState.Success).movies.isEmpty())
    }

    @Test
    fun `shows error when use case throws`() = runTest {
        every { useCase() } returns flow {
            throw IllegalStateException("Storage error")
        }

        val viewModel = FavoriteMoviesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FavoriteMoviesUiState.Error)
        assertEquals(
            UiText.DynamicString("Storage error"),
            (state as FavoriteMoviesUiState.Error).message,
        )
    }

    @Test
    fun `loadFavorites resubscribes to favorites`() = runTest {
        every { useCase() } returns flowOf(emptyList())
        val viewModel = FavoriteMoviesViewModel(useCase)
        advanceUntilIdle()

        every { useCase() } returns flowOf(listOf(fakeMovie))
        viewModel.loadFavorites()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FavoriteMoviesUiState.Success)
        assertEquals(1, (state as FavoriteMoviesUiState.Success).movies.size)
        verify(exactly = 2) { useCase() }
    }

    @Test
    fun `updates favorites when flow emits new list`() = runTest {
        val favorites = MutableStateFlow(emptyList<Movie>())
        every { useCase() } returns favorites
        val viewModel = FavoriteMoviesViewModel(useCase)
        advanceUntilIdle()

        favorites.value = listOf(fakeMovie)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FavoriteMoviesUiState.Success)
        assertEquals(1, (state as FavoriteMoviesUiState.Success).movies.size)
        assertEquals("Inception", state.movies[0].title)
    }
}
