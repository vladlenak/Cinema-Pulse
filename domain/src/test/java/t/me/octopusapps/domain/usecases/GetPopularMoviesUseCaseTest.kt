package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

class GetPopularMoviesUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = GetPopularMoviesUseCase(repository)

    @Test
    fun `invoke returns movie list from repository`() = runTest {
        val expected = MovieList(page = 1, results = emptyList())
        coEvery { repository.getPopularMovies() } returns expected

        val result = useCase()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke calls repository exactly once`() = runTest {
        coEvery { repository.getPopularMovies() } returns MovieList(page = 1, results = emptyList())

        useCase()

        coVerify(exactly = 1) { repository.getPopularMovies() }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.getPopularMovies() } throws Exception("Network error")

        useCase()
    }
}