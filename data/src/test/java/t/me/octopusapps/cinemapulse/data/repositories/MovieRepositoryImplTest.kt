package t.me.octopusapps.cinemapulse.data.repositories

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.cinemapulse.data.models.Genre
import t.me.octopusapps.cinemapulse.data.models.MovieDetails
import t.me.octopusapps.cinemapulse.data.models.MovieResponse
import t.me.octopusapps.cinemapulse.data.remote.MovieApi

class MovieRepositoryImplTest {

    private val api: MovieApi = mockk()
    private val repository = MovieRepositoryImpl(api)

    private val fakeMovieDetails = MovieDetails(
        id = 1,
        title = "Inception",
        overview = "A dream within a dream",
        popularity = 9.5,
        releaseDate = "2010-07-16",
        voteAverage = 8.8,
        voteCount = 30000,
        posterPath = "/poster.jpg",
        backdropPath = null,
        genres = listOf(Genre(id = 28, name = "Action")),
        adult = false,
        originalLanguage = "en",
        originalTitle = "Inception",
        video = false
    )

    private val fakeResponse = MovieResponse(
        page = 1,
        totalPages = 5,
        results = listOf(fakeMovieDetails)
    )

    // --- getPopularMovies ---

    @Test
    fun `getPopularMovies returns mapped movie list`() = runTest {
        coEvery { api.getPopularMovies(page = 1) } returns fakeResponse

        val result = repository.getPopularMovies(1)

        assertEquals(1, result.page)
        assertEquals(5, result.totalPages)
        assertEquals(1, result.results.size)
        assertEquals("Inception", result.results[0].title)
    }

    @Test
    fun `getPopularMovies passes correct page to api`() = runTest {
        coEvery { api.getPopularMovies(page = 3) } returns fakeResponse.copy(page = 3)

        repository.getPopularMovies(3)

        coVerify { api.getPopularMovies(page = 3) }
    }

    @Test(expected = Exception::class)
    fun `getPopularMovies propagates api exception`() = runTest {
        coEvery { api.getPopularMovies(any()) } throws Exception("Network error")

        repository.getPopularMovies(1)
    }

    // --- getMovieDetails ---

    @Test
    fun `getMovieDetails returns mapped movie`() = runTest {
        coEvery { api.getMovieDetails(1) } returns fakeMovieDetails

        val result = repository.getMovieDetails(1)

        assertEquals(1, result.id)
        assertEquals("Inception", result.title)
        assertEquals(listOf(28), result.genreIds)
    }

    @Test
    fun `getMovieDetails passes correct id to api`() = runTest {
        coEvery { api.getMovieDetails(42) } returns fakeMovieDetails.copy(id = 42)

        repository.getMovieDetails(42)

        coVerify { api.getMovieDetails(42) }
    }

    @Test(expected = Exception::class)
    fun `getMovieDetails propagates api exception`() = runTest {
        coEvery { api.getMovieDetails(any()) } throws Exception("Not found")

        repository.getMovieDetails(999)
    }

    // --- searchMovies ---

    @Test
    fun `searchMovies returns mapped results`() = runTest {
        coEvery { api.searchMovies("Inception") } returns fakeResponse

        val result = repository.searchMovies("Inception")

        assertEquals(1, result.results.size)
        assertEquals("Inception", result.results[0].title)
    }

    @Test
    fun `searchMovies passes correct query to api`() = runTest {
        coEvery { api.searchMovies("Batman") } returns fakeResponse

        repository.searchMovies("Batman")

        coVerify { api.searchMovies("Batman") }
    }

    @Test(expected = Exception::class)
    fun `searchMovies propagates api exception`() = runTest {
        coEvery { api.searchMovies(any()) } throws Exception("Network error")

        repository.searchMovies("Inception")
    }
}
