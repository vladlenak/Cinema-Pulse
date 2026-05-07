package t.me.octopusapps.cinemapulse.data.repositories

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.local.entities.FavoriteMovieEntity
import t.me.octopusapps.cinemapulse.data.local.entities.MovieDetailsEntity
import t.me.octopusapps.cinemapulse.data.local.entities.MovieEntity
import t.me.octopusapps.cinemapulse.data.local.entities.WatchedMovieEntity
import t.me.octopusapps.cinemapulse.data.models.Genre
import t.me.octopusapps.cinemapulse.data.models.MovieDetails
import t.me.octopusapps.cinemapulse.data.models.MovieResponse
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.domain.errors.MovieError
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory

class MovieRepositoryImplTest {

    private val api: MovieApi = mockk()
    private val movieDao: MovieDao = mockk(relaxed = true)
    private val repository = MovieRepositoryImpl(api, movieDao)
    private val cachePolicyRepository = MovieRepositoryImpl(
        api = api,
        movieDao = movieDao,
        currentTimeMillis = { NOW },
    )

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
        video = false,
    )

    private val fakeResponse = MovieResponse(
        page = 1,
        totalPages = 5,
        results = listOf(fakeMovieDetails),
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
        totalPages = 5,
    )

    private val fakeCachedDetailsEntity = MovieDetailsEntity(
        id = 1,
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
    )

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
        genreIds = listOf(28),
        adult = false,
        originalLanguage = "en",
        originalTitle = "Inception",
        video = false,
    )

    private val fakeFavoriteEntity = FavoriteMovieEntity(
        id = 1,
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
    )

    private val fakeWatchedEntity = WatchedMovieEntity(
        id = 1,
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
    fun `getMoviesByCategory returns fresh cache without network request`() = runTest {
        coEvery {
            movieDao.getMoviesByCategoryAndPage("TOP_RATED", 1)
        } returns listOf(
            fakeCachedEntity.copy(
                category = "TOP_RATED",
                title = "Cached Inception",
                cachedAt = FRESH_CACHED_AT,
            ),
        )

        val result = cachePolicyRepository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)

        assertEquals(1, result.results.size)
        assertEquals("Cached Inception", result.results[0].title)
        coVerify(exactly = 0) { api.getTopRatedMovies(any()) }
    }

    @Test
    fun `getMoviesByCategory refreshes stale cache from network`() = runTest {
        coEvery {
            movieDao.getMoviesByCategoryAndPage("TOP_RATED", 1)
        } returns listOf(
            fakeCachedEntity.copy(
                category = "TOP_RATED",
                title = "Stale Inception",
                cachedAt = STALE_CACHED_AT,
            ),
        )
        coEvery { api.getTopRatedMovies(page = 1) } returns fakeResponse

        val result = cachePolicyRepository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)

        assertEquals("Inception", result.results[0].title)
        coVerify { api.getTopRatedMovies(page = 1) }
        coVerify { movieDao.insertMovies(any()) }
    }

    @Test
    fun `getMoviesByCategory returns cached data when network fails`() = runTest {
        coEvery { api.getTopRatedMovies(any()) } throws Exception("Network error")
        coEvery {
            movieDao.getMoviesByCategoryAndPage("TOP_RATED", 1)
        } returns listOf(
            fakeCachedEntity.copy(
                category = "TOP_RATED",
                cachedAt = STALE_CACHED_AT,
            ),
        )

        val result = cachePolicyRepository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)

        assertEquals(1, result.results.size)
        assertEquals("Inception", result.results[0].title)
        coVerify { api.getTopRatedMovies(any()) }
    }

    @Test(expected = MovieError.Network::class)
    fun `getMoviesByCategory maps network error when cache is empty`() = runTest {
        coEvery { api.getTopRatedMovies(any()) } throws IOException("Network error")
        coEvery { movieDao.getMoviesByCategoryAndPage(any(), any()) } returns emptyList()

        repository.getMoviesByCategory(MovieCategory.TOP_RATED, 1)
    }

    @Test(expected = MovieError.Storage::class)
    fun `getMoviesByCategory maps cache read error when network fails`() = runTest {
        coEvery { api.getTopRatedMovies(any()) } throws IOException("Network error")
        coEvery {
            movieDao.getMoviesByCategoryAndPage("TOP_RATED", 1)
        } throws IllegalStateException("Storage error")

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

        coVerify {
            movieDao.insertMovieDetails(
                match {
                    it.id == 1 && it.title == "Inception" && it.genreIds == "28"
                },
            )
        }
    }

    @Test
    fun `getMovieDetails returns fresh cache without network request`() = runTest {
        coEvery { movieDao.getMovieDetailsById(1) } returns fakeCachedDetailsEntity.copy(
            title = "Cached Inception",
            cachedAt = FRESH_CACHED_AT,
        )

        val result = cachePolicyRepository.getMovieDetails(1)

        assertEquals("Cached Inception", result.title)
        coVerify(exactly = 0) { api.getMovieDetails(any()) }
    }

    @Test
    fun `getMovieDetails refreshes stale cache from network`() = runTest {
        coEvery { movieDao.getMovieDetailsById(1) } returns fakeCachedDetailsEntity.copy(
            title = "Stale Inception",
            cachedAt = STALE_CACHED_AT,
        )
        coEvery { api.getMovieDetails(1) } returns fakeMovieDetails

        val result = cachePolicyRepository.getMovieDetails(1)

        assertEquals("Inception", result.title)
        coVerify { api.getMovieDetails(1) }
        coVerify { movieDao.insertMovieDetails(any()) }
    }

    @Test
    fun `getMovieDetails returns cached movie when network fails`() = runTest {
        coEvery { api.getMovieDetails(1) } throws Exception("Not found")
        coEvery { movieDao.getMovieDetailsById(1) } returns fakeCachedDetailsEntity.copy(
            cachedAt = STALE_CACHED_AT,
        )

        val result = cachePolicyRepository.getMovieDetails(1)

        assertEquals(1, result.id)
        assertEquals("Inception", result.title)
        coVerify { movieDao.getMovieDetailsById(1) }
        coVerify { api.getMovieDetails(1) }
        coVerify(exactly = 0) { movieDao.getMoviesByCategoryAndPage(any(), any()) }
    }

    @Test(expected = MovieError.Network::class)
    fun `getMovieDetails maps network error when cache is empty`() = runTest {
        coEvery { api.getMovieDetails(any()) } throws IOException("Network error")
        coEvery { movieDao.getMovieDetailsById(any()) } returns null

        repository.getMovieDetails(999)
    }

    @Test(expected = MovieError.Storage::class)
    fun `getMovieDetails maps cache read error when network fails`() = runTest {
        coEvery { api.getMovieDetails(1) } throws IOException("Network error")
        coEvery { movieDao.getMovieDetailsById(1) } throws IllegalStateException("Storage error")

        repository.getMovieDetails(1)
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

    @Test(expected = MovieError.Network::class)
    fun `searchMovies maps network error`() = runTest {
        coEvery { api.searchMovies(any()) } throws IOException("Network error")

        repository.searchMovies("Inception")
    }

    // --- favorites ---

    @Test
    fun `getFavoriteMovies returns mapped favorite movies`() = runTest {
        coEvery { movieDao.getFavoriteMovies() } returns listOf(fakeFavoriteEntity)

        val result = repository.getFavoriteMovies()

        assertEquals(1, result.size)
        assertEquals("Inception", result[0].title)
        assertEquals(listOf(28), result[0].genreIds)
    }

    @Test(expected = MovieError.Storage::class)
    fun `getFavoriteMovies maps dao exception to storage error`() = runTest {
        coEvery { movieDao.getFavoriteMovies() } throws IllegalStateException("Storage error")

        repository.getFavoriteMovies()
    }

    @Test
    fun `isMovieFavorite returns favorite state from dao`() = runTest {
        coEvery { movieDao.isMovieFavorite(1) } returns true

        val result = repository.isMovieFavorite(1)

        assertEquals(true, result)
    }

    @Test(expected = MovieError.Storage::class)
    fun `isMovieFavorite maps dao exception to storage error`() = runTest {
        coEvery { movieDao.isMovieFavorite(1) } throws IllegalStateException("Storage error")

        repository.isMovieFavorite(1)
    }

    @Test
    fun `addFavoriteMovie saves favorite movie`() = runTest {
        repository.addFavoriteMovie(fakeMovie)

        coVerify {
            movieDao.insertFavoriteMovie(
                match {
                    it.id == 1 && it.title == "Inception" && it.genreIds == "28"
                },
            )
        }
    }

    @Test(expected = MovieError.Storage::class)
    fun `addFavoriteMovie maps dao exception to storage error`() = runTest {
        coEvery { movieDao.insertFavoriteMovie(any()) } throws IllegalStateException(
            "Storage error",
        )

        repository.addFavoriteMovie(fakeMovie)
    }

    @Test
    fun `removeFavoriteMovie deletes favorite movie`() = runTest {
        repository.removeFavoriteMovie(1)

        coVerify { movieDao.deleteFavoriteMovie(1) }
    }

    @Test(expected = MovieError.Storage::class)
    fun `removeFavoriteMovie maps dao exception to storage error`() = runTest {
        coEvery { movieDao.deleteFavoriteMovie(1) } throws IllegalStateException("Storage error")

        repository.removeFavoriteMovie(1)
    }

    // --- watched ---

    @Test
    fun `getWatchedMovies returns mapped watched movies`() = runTest {
        coEvery { movieDao.getWatchedMovies() } returns listOf(fakeWatchedEntity)

        val result = repository.getWatchedMovies()

        assertEquals(1, result.size)
        assertEquals("Inception", result[0].title)
        assertEquals(listOf(28), result[0].genreIds)
    }

    @Test(expected = MovieError.Storage::class)
    fun `getWatchedMovies maps dao exception to storage error`() = runTest {
        coEvery { movieDao.getWatchedMovies() } throws IllegalStateException("Storage error")

        repository.getWatchedMovies()
    }

    @Test
    fun `isMovieWatched returns watched state from dao`() = runTest {
        coEvery { movieDao.isMovieWatched(1) } returns true

        val result = repository.isMovieWatched(1)

        assertEquals(true, result)
    }

    @Test(expected = MovieError.Storage::class)
    fun `isMovieWatched maps dao exception to storage error`() = runTest {
        coEvery { movieDao.isMovieWatched(1) } throws IllegalStateException("Storage error")

        repository.isMovieWatched(1)
    }

    @Test
    fun `addWatchedMovie saves watched movie`() = runTest {
        repository.addWatchedMovie(fakeMovie)

        coVerify {
            movieDao.insertWatchedMovie(
                match {
                    it.id == 1 && it.title == "Inception" && it.genreIds == "28"
                },
            )
        }
    }

    @Test(expected = MovieError.Storage::class)
    fun `addWatchedMovie maps dao exception to storage error`() = runTest {
        coEvery { movieDao.insertWatchedMovie(any()) } throws IllegalStateException(
            "Storage error",
        )

        repository.addWatchedMovie(fakeMovie)
    }

    @Test
    fun `removeWatchedMovie deletes watched movie`() = runTest {
        repository.removeWatchedMovie(1)

        coVerify { movieDao.deleteWatchedMovie(1) }
    }

    @Test(expected = MovieError.Storage::class)
    fun `removeWatchedMovie maps dao exception to storage error`() = runTest {
        coEvery { movieDao.deleteWatchedMovie(1) } throws IllegalStateException("Storage error")

        repository.removeWatchedMovie(1)
    }

    private companion object {
        const val NOW = 1_700_000_000_000L
        const val FRESH_CACHED_AT = NOW - 60_000L
        const val STALE_CACHED_AT = NOW - 25 * 60 * 60 * 1000L
    }
}
