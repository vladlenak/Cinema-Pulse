package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import t.me.octopusapps.domain.repositories.MovieRepository

class IsMovieFavoriteUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = IsMovieFavoriteUseCase(repository)

    @Test
    fun `invoke returns true when movie is favorite`() = runTest {
        coEvery { repository.isMovieFavorite(1) } returns true

        val result = useCase(1)

        assertTrue(result)
    }

    @Test
    fun `invoke returns false when movie is not favorite`() = runTest {
        coEvery { repository.isMovieFavorite(1) } returns false

        val result = useCase(1)

        assertFalse(result)
    }

    @Test
    fun `invoke passes movie id to repository`() = runTest {
        coEvery { repository.isMovieFavorite(42) } returns true

        useCase(42)

        coVerify { repository.isMovieFavorite(42) }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.isMovieFavorite(any()) } throws Exception("Storage error")

        useCase(1)
    }
}
