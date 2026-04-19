package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

class SearchMoviesUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = SearchMoviesUseCase(repository)

    @Test
    fun `invoke returns results for given query`() = runTest {
        val expected = MovieList(page = 1, results = emptyList())
        coEvery { repository.searchMovies("Inception") } returns expected

        val result = useCase("Inception")

        assertEquals(expected, result)
    }

    @Test
    fun `invoke passes correct query to repository`() = runTest {
        coEvery { repository.searchMovies(any()) } returns MovieList(
            page = 1,
            results = emptyList()
        )

        useCase("Batman")

        coVerify { repository.searchMovies("Batman") }
    }

    @Test
    fun `invoke returns empty results for unknown query`() = runTest {
        val empty = MovieList(page = 1, results = emptyList())
        coEvery { repository.searchMovies("xyzxyzxyz") } returns empty

        val result = useCase("xyzxyzxyz")

        assertEquals(0, result.results.size)
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.searchMovies(any()) } throws Exception("Network error")

        useCase("Inception")
    }
}