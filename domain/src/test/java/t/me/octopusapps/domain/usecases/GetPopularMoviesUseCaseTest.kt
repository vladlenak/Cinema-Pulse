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
        val expected = MovieList(page = 1, results = emptyList(), totalPages = 1)
        coEvery { repository.getPopularMovies(1) } returns expected

        val result = useCase(1)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke calls repository exactly once`() = runTest {
        coEvery { repository.getPopularMovies(1) } returns MovieList(
            page = 1,
            results = emptyList(),
            totalPages = 1
        )

        useCase(1)

        coVerify(exactly = 1) { repository.getPopularMovies(1) }
    }

    @Test
    fun `invoke passes correct page to repository`() = runTest {
        coEvery { repository.getPopularMovies(2) } returns MovieList(
            page = 2,
            results = emptyList(),
            totalPages = 10
        )

        val result = useCase(2)

        assertEquals(2, result.page)
        coVerify { repository.getPopularMovies(2) }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.getPopularMovies(any()) } throws Exception("Network error")

        useCase(1)
    }
}