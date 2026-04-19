package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

public class SearchMoviesUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(query: String): MovieList =
        repository.searchMovies(query)
}