package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

class GetMovieDetailsUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = GetMovieDetailsUseCase(repository)

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
    fun `invoke returns movie for given id`() = runTest {
        coEvery { repository.getMovieDetails(1) } returns fakeMovie

        val result = useCase(1)

        assertEquals(fakeMovie, result)
    }

    @Test
    fun `invoke passes correct movie id to repository`() = runTest {
        coEvery { repository.getMovieDetails(42) } returns fakeMovie

        useCase(42)

        coVerify { repository.getMovieDetails(42) }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.getMovieDetails(any()) } throws Exception("Not found")

        useCase(999)
    }
}