package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import t.me.octopusapps.domain.repositories.MovieRepository

class IsMovieWatchedUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = IsMovieWatchedUseCase(repository)

    @Test
    fun `invoke returns true when movie is watched`() = runTest {
        coEvery { repository.isMovieWatched(1) } returns true

        val result = useCase(1)

        assertTrue(result)
    }

    @Test
    fun `invoke returns false when movie is not watched`() = runTest {
        coEvery { repository.isMovieWatched(1) } returns false

        val result = useCase(1)

        assertFalse(result)
    }

    @Test
    fun `invoke passes movie id to repository`() = runTest {
        coEvery { repository.isMovieWatched(42) } returns true

        useCase(42)

        coVerify { repository.isMovieWatched(42) }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.isMovieWatched(any()) } throws Exception("Storage error")

        useCase(1)
    }
}
