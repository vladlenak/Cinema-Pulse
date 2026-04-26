package t.me.octopusapps.domain.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

class GetMoviesByCategoryUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = GetMoviesByCategoryUseCase(repository)

    private fun fakeMovieList(page: Int = 1) = MovieList(
        page = page,
        totalPages = 5,
        results = listOf(
            Movie(
                id = 1, title = "Inception", overview = "Overview",
                popularity = 9.0, releaseDate = "2010-07-16",
                voteAverage = 8.8, voteCount = 30000,
                posterPath = null, backdropPath = null,
                genreIds = listOf(28), adult = false,
                originalLanguage = "en", originalTitle = "Inception", video = false
            )
        )
    )

    @Test
    fun `invoke returns movie list for popular category`() = runTest {
        val expected = fakeMovieList()
        coEvery { repository.getMoviesByCategory(MovieCategory.POPULAR, 1) } returns expected

        val result = useCase(MovieCategory.POPULAR, 1)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns movie list for top rated category`() = runTest {
        val expected = fakeMovieList()
        coEvery { repository.getMoviesByCategory(MovieCategory.TOP_RATED, 1) } returns expected

        val result = useCase(MovieCategory.TOP_RATED, 1)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns movie list for upcoming category`() = runTest {
        val expected = fakeMovieList()
        coEvery { repository.getMoviesByCategory(MovieCategory.UPCOMING, 1) } returns expected

        val result = useCase(MovieCategory.UPCOMING, 1)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns movie list for now playing category`() = runTest {
        val expected = fakeMovieList()
        coEvery { repository.getMoviesByCategory(MovieCategory.NOW_PLAYING, 1) } returns expected

        val result = useCase(MovieCategory.NOW_PLAYING, 1)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke passes correct category and page to repository`() = runTest {
        coEvery { repository.getMoviesByCategory(MovieCategory.TOP_RATED, 3) } returns fakeMovieList(3)

        useCase(MovieCategory.TOP_RATED, 3)

        coVerify { repository.getMoviesByCategory(MovieCategory.TOP_RATED, 3) }
    }

    @Test(expected = Exception::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.getMoviesByCategory(any(), any()) } throws Exception("Network error")

        useCase(MovieCategory.POPULAR, 1)
    }
}