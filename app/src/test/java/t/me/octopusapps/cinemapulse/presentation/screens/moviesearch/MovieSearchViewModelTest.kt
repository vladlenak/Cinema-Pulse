package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import t.me.octopusapps.cinemapulse.presentation.text.UiText
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MovieSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: SearchMoviesUseCase = mockk()
    private lateinit var viewModel: MovieSearchViewModel

    private fun fakeMovieList(title: String = "Inception") = MovieList(
        page = 1,
        totalPages = 1,
        results = listOf(
            Movie(
                id = 1,
                title = title,
                overview = "Overview",
                popularity = 9.0,
                releaseDate = "2010-07-16",
                voteAverage = 8.8,
                voteCount = 30000,
                posterPath = "/poster.jpg",
                backdropPath = null,
                genreIds = listOf(28),
                adult = false,
                originalLanguage = "en",
                originalTitle = title,
                video = false,
            ),
        ),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieSearchViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty success`() {
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `onQueryChanged with blank query resets to empty success`() = runTest {
        viewModel.onQueryChanged("Inception")
        advanceTimeBy(100L)

        viewModel.onQueryChanged("")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `onQueryChanged stores query in ui state immediately`() {
        viewModel.onQueryChanged("Inception")

        val state = viewModel.uiState.value
        assertEquals("Inception", state.query)
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `search triggers after debounce delay`() = runTest {
        coEvery { useCase("Inception") } returns fakeMovieList()

        viewModel.onQueryChanged("Inception")
        advanceTimeBy(401L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Inception", state.query)
        assertEquals(1, state.movies.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `search does not trigger before debounce delay`() = runTest {
        coEvery { useCase(any()) } returns fakeMovieList()

        viewModel.onQueryChanged("Inc")
        advanceTimeBy(200L)

        coVerify(exactly = 0) { useCase(any()) }
    }

    @Test
    fun `clearQuery resets state and cancels pending search`() = runTest {
        coEvery { useCase(any()) } returns fakeMovieList()

        viewModel.onQueryChanged("Inception")
        viewModel.clearQuery()
        advanceTimeBy(401L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
        coVerify(exactly = 0) { useCase(any()) }
    }

    @Test
    fun `rapid typing only triggers one search`() = runTest {
        coEvery { useCase("Batman") } returns fakeMovieList("Batman")

        viewModel.onQueryChanged("B")
        advanceTimeBy(100L)
        viewModel.onQueryChanged("Ba")
        advanceTimeBy(100L)
        viewModel.onQueryChanged("Bat")
        advanceTimeBy(100L)
        viewModel.onQueryChanged("Batman")
        advanceTimeBy(401L)
        advanceUntilIdle()

        coVerify(exactly = 1) { useCase("Batman") }
    }

    @Test
    fun `same query does not trigger duplicate search`() = runTest {
        coEvery { useCase("Inception") } returns fakeMovieList()

        viewModel.onQueryChanged("Inception")
        advanceTimeBy(401L)
        advanceUntilIdle()

        viewModel.onQueryChanged("Inception")
        advanceTimeBy(401L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Inception", state.query)
        assertEquals(1, state.movies.size)
        coVerify(exactly = 1) { useCase("Inception") }
    }

    @Test
    fun `new query cancels stale in-flight search`() = runTest {
        val staleResult = CompletableDeferred<MovieList>()
        coEvery { useCase("Inception") } coAnswers { staleResult.await() }
        coEvery { useCase("Batman") } returns fakeMovieList("Batman")

        viewModel.onQueryChanged("Inception")
        advanceTimeBy(401L)

        viewModel.onQueryChanged("Batman")
        advanceTimeBy(401L)
        staleResult.complete(fakeMovieList("Inception"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Batman", state.query)
        assertEquals("Batman", state.movies.single().title)
        assertFalse(state.isLoading)
        assertNull(state.error)
        coVerify(exactly = 1) { useCase("Inception") }
        coVerify(exactly = 1) { useCase("Batman") }
    }

    @Test
    fun `shows error state when use case throws`() = runTest {
        coEvery { useCase(any()) } throws Exception("Network error")

        viewModel.onQueryChanged("Inception")
        advanceTimeBy(401L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Inception", state.query)
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(UiText.DynamicString("Network error"), state.error)
    }

    @Test
    fun `retry repeats current query after error`() = runTest {
        coEvery { useCase("Inception") } throws Exception("Network error")

        viewModel.onQueryChanged("Inception")
        advanceTimeBy(401L)
        advanceUntilIdle()

        coEvery { useCase("Inception") } returns fakeMovieList()
        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Inception", state.query)
        assertEquals(1, state.movies.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
        coVerify(exactly = 2) { useCase("Inception") }
    }
}
