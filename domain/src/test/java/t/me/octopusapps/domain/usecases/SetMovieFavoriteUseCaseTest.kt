package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

class SetMovieFavoriteUseCaseTest {

    private val repository: MovieRepository = mockk(relaxed = true)
    private val useCase = SetMovieFavoriteUseCase(repository)

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
    fun `invoke adds movie when favorite state is true`() = runTest {
        useCase(fakeMovie, true)

        coVerify { repository.addFavoriteMovie(fakeMovie) }
        coVerify(exactly = 0) { repository.removeFavoriteMovie(any()) }
    }

    @Test
    fun `invoke removes movie when favorite state is false`() = runTest {
        useCase(fakeMovie, false)

        coVerify { repository.removeFavoriteMovie(fakeMovie.id) }
        coVerify(exactly = 0) { repository.addFavoriteMovie(any()) }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates add exception from repository`() = runTest {
        coEvery { repository.addFavoriteMovie(any()) } throws Exception("Storage error")

        useCase(fakeMovie, true)
    }

    @Test(expected = Exception::class)
    fun `invoke propagates remove exception from repository`() = runTest {
        coEvery { repository.removeFavoriteMovie(any()) } throws Exception("Storage error")

        useCase(fakeMovie, false)
    }
}
