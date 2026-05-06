package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

class SetMovieWatchedUseCaseTest {

    private val repository: MovieRepository = mockk(relaxed = true)
    private val useCase = SetMovieWatchedUseCase(repository)

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
    fun `invoke adds movie when watched state is true`() = runTest {
        useCase(fakeMovie, true)

        coVerify { repository.addWatchedMovie(fakeMovie) }
        coVerify(exactly = 0) { repository.removeWatchedMovie(any()) }
    }

    @Test
    fun `invoke removes movie when watched state is false`() = runTest {
        useCase(fakeMovie, false)

        coVerify { repository.removeWatchedMovie(fakeMovie.id) }
        coVerify(exactly = 0) { repository.addWatchedMovie(any()) }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates add exception from repository`() = runTest {
        coEvery { repository.addWatchedMovie(any()) } throws Exception("Storage error")

        useCase(fakeMovie, true)
    }

    @Test(expected = Exception::class)
    fun `invoke propagates remove exception from repository`() = runTest {
        coEvery { repository.removeWatchedMovie(any()) } throws Exception("Storage error")

        useCase(fakeMovie, false)
    }
}
