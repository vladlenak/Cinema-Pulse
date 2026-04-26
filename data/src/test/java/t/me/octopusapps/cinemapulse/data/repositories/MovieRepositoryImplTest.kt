package t.me.octopusapps.cinemapulse.data.repositories

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.local.entities.MovieEntity
import t.me.octopusapps.cinemapulse.data.models.Genre
import t.me.octopusapps.cinemapulse.data.models.MovieDetails
import t.me.octopusapps.cinemapulse.data.models.MovieResponse
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.domain.models.MovieCategory

class MovieRepositoryImplTest {

    private val api: MovieApi = mockk()
    private val movieDao: MovieDao = mockk(relaxed = true)
    private val repository = MovieRepositoryImpl(api, movieDao)

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

    private val fakeCachedEntity = MovieEntity(
        id = 1,
        category = "POPULAR",
        title = "Inception",
        overview = "A dream within a dream",
        popularity = 9.5,
        releaseDate = "2010-07-16",
        voteAverage = 8.8,
        voteCount = 30000,
        posterPath = "/poster.jpg",
        backdropPath = null,
        genreIds = "28",
        adult = false,
        originalLanguage = "en",
        originalTitle = "Inception",
        video = false,
        page = 1,
        totalPages = 5
    )

    // --- getPopularMovies ---

    @Test
    fun `getPopularMovies returns mapped movie list from network`() = runTest {
        coEvery { api.getPopularMovies(page = 1) } returns fakeResponse

        val result = repository.getPopularMovies(1)

        assertEquals(1, result.page)
        assertEquals(5, result.totalPages)
        assertEquals(1, result.results.size)
        assertEquals("Inception", result.results[0].title)
    }

    @Test
    fun `getPopularMovies saves result to cache`() = runTest {
        coEvery { api.getPopularMovies(page = 1) } returns fakeResponse

        repository.getPopularMovies(1)

        coVerify { movieDao.insertMovies(any()) }
    }

    // --- getMoviesByCategory ---

    @Test
    fun `getMoviesByCategory returns top rated from network`() = runTest {
        coEvery { api.getTopRatedMovies(page = 1) } returns fakeResponse

        val result = repository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)

        assertEquals(1, result.results.size)
        assertEquals("Inception", result.results[0].title)
    }

    @Test
    fun `getMoviesByCategory returns upcoming from network`() = runTest {
        coEvery { api.getUpcomingMovies(page = 1) } returns fakeResponse

        val result = repository.getMoviesByCategory(MovieCategory.UPCOMING, 1)

        assertEquals(1, result.results.size)
    }

    @Test
    fun `getMoviesByCategory returns now playing from network`() = runTest {
        coEvery { api.getNowPlayingMovies(page = 1) } returns fakeResponse

        val result = repository.getMoviesByCategory(MovieCategory.NOW_PLAYING, 1)

        assertEquals(1, result.results.size)
    }

    @Test
    fun `getMoviesByCategory saves result to cache`() = runTest {
        coEvery { api.getTopRatedMovies(page = 1) } returns fakeResponse

        repository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)

        coVerify { movieDao.insertMovies(any()) }
    }

    @Test
    fun `getMoviesByCategory returns cached data when network fails`() = runTest {
        coEvery { api.getTopRatedMovies(any()) } throws Exception("Network error")
        coEvery {
            movieDao.getMoviesByCategoryAndPage("TOP_RATED", 1)
        } returns listOf(fakeCachedEntity.copy(category = "TOP_RATED"))

        val result = repository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)

        assertEquals(1, result.results.size)
        assertEquals("Inception", result.results[0].title)
    }

    @Test(expected = Exception::class)
    fun `getMoviesByCategory throws when network fails and cache is empty`() = runTest {
        coEvery { api.getTopRatedMovies(any()) } throws Exception("Network error")
        coEvery { movieDao.getMoviesByCategoryAndPage(any(), any()) } returns emptyList()

        repository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)
    }

    // --- getMovieDetails ---

    @Test
    fun `getMovieDetails returns mapped movie from network`() = runTest {
        coEvery { api.getMovieDetails(1) } returns fakeMovieDetails

        val result = repository.getMovieDetails(1)

        assertEquals(1, result.id)
        assertEquals("Inception", result.title)
        assertEquals(listOf(28), result.genreIds)
    }

    @Test
    fun `getMovieDetails saves result to cache`() = runTest {
        coEvery { api.getMovieDetails(1) } returns fakeMovieDetails

        repository.getMovieDetails(1)

        coVerify { movieDao.insertMovie(any()) }
    }

    @Test
    fun `getMovieDetails returns cached movie when network fails`() = runTest {
        coEvery { api.getMovieDetails(1) } throws Exception("Not found")
        coEvery { movieDao.getMovieById(1) } returns fakeCachedEntity

        val result = repository.getMovieDetails(1)

        assertEquals(1, result.id)
        assertEquals("Inception", result.title)
    }

    @Test(expected = Exception::class)
    fun `getMovieDetails throws when network fails and cache is empty`() = runTest {
        coEvery { api.getMovieDetails(any()) } throws Exception("Not found")
        coEvery { movieDao.getMovieById(any()) } returns null

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