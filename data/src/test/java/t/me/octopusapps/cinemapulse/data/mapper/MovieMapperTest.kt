package t.me.octopusapps.cinemapulse.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import t.me.octopusapps.cinemapulse.data.models.Genre
import t.me.octopusapps.cinemapulse.data.models.MovieDetails
import t.me.octopusapps.cinemapulse.data.models.MovieResponse

class MovieMapperTest {

    private val fakeMovieDetails = MovieDetails(
        id = 1,
        title = "Inception",
        overview = "A dream within a dream",
        popularity = 9.5,
        releaseDate = "2010-07-16",
        voteAverage = 8.8,
        voteCount = 30000,
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        genres = listOf(Genre(id = 28, name = "Action"), Genre(id = 878, name = "Sci-Fi")),
        adult = false,
        originalLanguage = "en",
        originalTitle = "Inception",
        video = false
    )

    // --- MovieDetails.mapToMovie() ---

    @Test
    fun `mapToMovie maps all fields correctly`() {
        val movie = fakeMovieDetails.mapToMovie()

        assertEquals(1, movie.id)
        assertEquals("Inception", movie.title)
        assertEquals("A dream within a dream", movie.overview)
        assertEquals(9.5, movie.popularity, 0.0)
        assertEquals("2010-07-16", movie.releaseDate)
        assertEquals(8.8, movie.voteAverage, 0.0)
        assertEquals(30000, movie.voteCount)
        assertEquals("/poster.jpg", movie.posterPath)
        assertEquals("/backdrop.jpg", movie.backdropPath)
        assertEquals(false, movie.adult)
        assertEquals("en", movie.originalLanguage)
        assertEquals("Inception", movie.originalTitle)
        assertEquals(false, movie.video)
    }

    @Test
    fun `mapToMovie maps genre ids from genres list`() {
        val movie = fakeMovieDetails.mapToMovie()

        assertEquals(listOf(28, 878), movie.genreIds)
    }

    @Test
    fun `mapToMovie returns empty genreIds when genres is null`() {
        val movie = fakeMovieDetails.copy(genres = null).mapToMovie()

        assertTrue(movie.genreIds!!.isEmpty())
    }

    @Test
    fun `mapToMovie returns empty genreIds when genres is empty`() {
        val movie = fakeMovieDetails.copy(genres = emptyList()).mapToMovie()

        assertTrue(movie.genreIds!!.isEmpty())
    }

    @Test
    fun `mapToMovie preserves null posterPath`() {
        val movie = fakeMovieDetails.copy(posterPath = null).mapToMovie()

        assertNull(movie.posterPath)
    }

    @Test
    fun `mapToMovie preserves null backdropPath`() {
        val movie = fakeMovieDetails.copy(backdropPath = null).mapToMovie()

        assertNull(movie.backdropPath)
    }

    // --- MovieResponse.mapToMovieList() ---

    @Test
    fun `mapToMovieList maps page and totalPages correctly`() {
        val response = MovieResponse(
            page = 2,
            totalPages = 10,
            results = listOf(fakeMovieDetails)
        )

        val movieList = response.mapToMovieList()

        assertEquals(2, movieList.page)
        assertEquals(10, movieList.totalPages)
    }

    @Test
    fun `mapToMovieList maps all results`() {
        val response = MovieResponse(
            page = 1,
            totalPages = 5,
            results = listOf(
                fakeMovieDetails,
                fakeMovieDetails.copy(id = 2, title = "Interstellar")
            )
        )

        val movieList = response.mapToMovieList()

        assertEquals(2, movieList.results.size)
        assertEquals("Inception", movieList.results[0].title)
        assertEquals("Interstellar", movieList.results[1].title)
    }

    @Test
    fun `mapToMovieList returns empty results for empty response`() {
        val response = MovieResponse(page = 1, totalPages = 1, results = emptyList())

        val movieList = response.mapToMovieList()

        assertTrue(movieList.results.isEmpty())
    }
}