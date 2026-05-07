package t.me.octopusapps.domain.usecases

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

class GetWatchedMoviesUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = GetWatchedMoviesUseCase(repository)

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

    @Test
    fun `invoke returns watched movies`() = runTest {
        every { repository.getWatchedMovies() } returns flowOf(listOf(fakeMovie))

        val result = useCase().first()

        assertEquals(listOf(fakeMovie), result)
    }

    @Test
    fun `invoke requests watched movies from repository`() = runTest {
        every { repository.getWatchedMovies() } returns flowOf(emptyList())

        useCase().first()

        verify { repository.getWatchedMovies() }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        every { repository.getWatchedMovies() } returns flow {
            throw IllegalStateException("Storage error")
        }

        useCase().first()
    }
}
