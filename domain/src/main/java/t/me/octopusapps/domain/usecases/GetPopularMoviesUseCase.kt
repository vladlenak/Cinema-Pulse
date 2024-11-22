package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

public class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(apiKey: String): MovieList =
        repository.getPopularMovies(apiKey)
}