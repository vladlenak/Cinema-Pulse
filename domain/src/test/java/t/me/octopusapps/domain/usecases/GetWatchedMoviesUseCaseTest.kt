package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
        video = false
    )

    @Test
    fun `invoke returns watched movies`() = runTest {
        coEvery { repository.getWatchedMovies() } returns listOf(fakeMovie)

        val result = useCase()

        assertEquals(listOf(fakeMovie), result)
    }

    @Test
    fun `invoke requests watched movies from repository`() = runTest {
        coEvery { repository.getWatchedMovies() } returns emptyList()

        useCase()

        coVerify { repository.getWatchedMovies() }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.getWatchedMovies() } throws Exception("Storage error")

        useCase()
    }
}
