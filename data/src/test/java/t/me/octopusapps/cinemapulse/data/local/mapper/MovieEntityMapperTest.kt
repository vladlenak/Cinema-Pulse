package t.me.octopusapps.cinemapulse.data.local.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import t.me.octopusapps.cinemapulse.data.local.entities.FavoriteMovieEntity
import t.me.octopusapps.cinemapulse.data.local.entities.MovieDetailsEntity
import t.me.octopusapps.cinemapulse.data.local.entities.WatchedMovieEntity
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory

class MovieEntityMapperTest {

    private val fakeMovie = Movie(
        id = 1,
        title = "Inception",
        overview = "A dream within a dream",
        popularity = 9.5,
        releaseDate = "2010-07-16",
        voteAverage = 8.8,
        voteCount = 30000,
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        genreIds = listOf(28, 878),
        adult = false,
        originalLanguage = "en",
        originalTitle = "Inception",
        video = false,
    )

    @Test
    fun `toEntity maps cache metadata and movie fields`() {
        val entity = fakeMovie.toEntity(
            category = MovieCategory.TOP_RATED,
            page = 2,
            totalPages = 10,
        )

        assertEquals(1, entity.id)
        assertEquals("TOP_RATED", entity.category)
        assertEquals("Inception", entity.title)
        assertEquals("A dream within a dream", entity.overview)
        assertEquals(9.5, entity.popularity, 0.0)
        assertEquals("2010-07-16", entity.releaseDate)
        assertEquals(8.8, entity.voteAverage, 0.0)
        assertEquals(30000, entity.voteCount)
        assertEquals("/poster.jpg", entity.posterPath)
        assertEquals("/backdrop.jpg", entity.backdropPath)
        assertEquals("28,878", entity.genreIds)
        assertEquals(false, entity.adult)
        assertEquals("en", entity.originalLanguage)
        assertEquals("Inception", entity.originalTitle)
        assertEquals(false, entity.video)
        assertEquals(2, entity.page)
        assertEquals(10, entity.totalPages)
    }

    @Test
    fun `movie entity maps back to domain movie`() {
        val entity = fakeMovie.toEntity(
            category = MovieCategory.POPULAR,
            page = 1,
            totalPages = 5,
        )

        val movie = entity.toDomain()

        assertEquals(fakeMovie, movie)
    }

    @Test
    fun `details entity maps back to domain movie`() {
        val entity = fakeMovie.toDetailsEntity()

        val movie = entity.toDomain()

        assertEquals(fakeMovie, movie)
    }

    @Test
    fun `favorite entity maps back to domain movie`() {
        val entity = fakeMovie.toFavoriteEntity()

        val movie = entity.toDomain()

        assertEquals(fakeMovie, movie)
    }

    @Test
    fun `watched entity maps back to domain movie`() {
        val entity = fakeMovie.toWatchedEntity()

        val movie = entity.toDomain()

        assertEquals(fakeMovie, movie)
    }

    @Test
    fun `domain movie with null genres keeps null genre storage value`() {
        val movie = fakeMovie.copy(genreIds = null)

        val entity = movie.toEntity(
            category = MovieCategory.POPULAR,
            page = 1,
            totalPages = 5,
        )

        assertNull(entity.genreIds)
        assertNull(entity.toDomain().genreIds)
    }

    @Test
    fun `malformed stored genre ids are ignored when mapping movie entity`() {
        val entity = fakeMovie.toEntity(
            category = MovieCategory.POPULAR,
            page = 1,
            totalPages = 5,
        ).copy(genreIds = "28,broken,878")

        val movie = entity.toDomain()

        assertEquals(listOf(28, 878), movie.genreIds)
    }

    @Test
    fun `malformed stored genre ids are ignored for all local movie tables`() {
        assertEquals(
            listOf(28, 878),
            movieDetailsEntity(genreIds = "28,broken,878").toDomain().genreIds,
        )
        assertEquals(
            listOf(28, 878),
            favoriteMovieEntity(genreIds = "28,broken,878").toDomain().genreIds,
        )
        assertEquals(
            listOf(28, 878),
            watchedMovieEntity(genreIds = "28,broken,878").toDomain().genreIds,
        )
    }

    private fun movieDetailsEntity(genreIds: String?): MovieDetailsEntity =
        fakeMovie.toDetailsEntity().copy(genreIds = genreIds)

    private fun favoriteMovieEntity(genreIds: String?): FavoriteMovieEntity =
        fakeMovie.toFavoriteEntity().copy(genreIds = genreIds)

    private fun watchedMovieEntity(genreIds: String?): WatchedMovieEntity =
        fakeMovie.toWatchedEntity().copy(genreIds = genreIds)
}
