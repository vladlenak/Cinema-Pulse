package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.usecases.GetMovieDetailsUseCase
import t.me.octopusapps.domain.usecases.IsMovieFavoriteUseCase
import t.me.octopusapps.domain.usecases.SetMovieFavoriteUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase = mockk()
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase = mockk()
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase = mockk(relaxed = true)

    private lateinit var viewModel: MovieDetailsViewModel

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
        video = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieDetailsViewModel(
            getMovieDetailsUseCase,
            isMovieFavoriteUseCase,
            setMovieFavoriteUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() {
        assertTrue(viewModel.uiState.value is MovieDetailsUiState.Loading)
    }

    @Test
    fun `fetchMovieDetails loads movie and favorite state`() = runTest {
        coEvery { getMovieDetailsUseCase(1) } returns fakeMovie
        coEvery { isMovieFavoriteUseCase(1) } returns true

        viewModel.fetchMovieDetails(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieDetailsUiState.Success)
        state as MovieDetailsUiState.Success
        assertEquals("Inception", state.movie.title)
        assertTrue(state.isFavorite)
        assertFalse(state.isFavoriteUpdating)
    }

    @Test
    fun `fetchMovieDetails shows error when details use case throws`() = runTest {
        coEvery { getMovieDetailsUseCase(1) } throws Exception("Not found")

        viewModel.fetchMovieDetails(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieDetailsUiState.Error)
        assertEquals("Not found", (state as MovieDetailsUiState.Error).message)
        coVerify(exactly = 0) { isMovieFavoriteUseCase(any()) }
    }

    @Test
    fun `onFavoriteClick adds movie to favorites`() = runTest {
        coEvery { getMovieDetailsUseCase(1) } returns fakeMovie
        coEvery { isMovieFavoriteUseCase(1) } returns false

        viewModel.fetchMovieDetails(1)
        advanceUntilIdle()
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value as MovieDetailsUiState.Success
        assertTrue(state.isFavorite)
        assertFalse(state.isFavoriteUpdating)
        coVerify {
            setMovieFavoriteUseCase(
                match { it.id == fakeMovie.id && it.title == fakeMovie.title },
                true
            )
        }
    }

    @Test
    fun `onFavoriteClick removes movie from favorites`() = runTest {
        coEvery { getMovieDetailsUseCase(1) } returns fakeMovie
        coEvery { isMovieFavoriteUseCase(1) } returns true

        viewModel.fetchMovieDetails(1)
        advanceUntilIdle()
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value as MovieDetailsUiState.Success
        assertFalse(state.isFavorite)
        assertFalse(state.isFavoriteUpdating)
        coVerify {
            setMovieFavoriteUseCase(
                match { it.id == fakeMovie.id && it.title == fakeMovie.title },
                false
            )
        }
    }

    @Test
    fun `onFavoriteClick restores previous state when favorite update fails`() = runTest {
        coEvery { getMovieDetailsUseCase(1) } returns fakeMovie
        coEvery { isMovieFavoriteUseCase(1) } returns false
        coEvery { setMovieFavoriteUseCase(any(), true) } throws Exception("Storage error")

        viewModel.fetchMovieDetails(1)
        advanceUntilIdle()
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value as MovieDetailsUiState.Success
        assertFalse(state.isFavorite)
        assertFalse(state.isFavoriteUpdating)
    }

    @Test
    fun `onFavoriteClick does nothing before movie is loaded`() = runTest {
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        coVerify(exactly = 0) { setMovieFavoriteUseCase(any(), any()) }
    }
}
