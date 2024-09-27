package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.repositories.MovieRepository

class SearchMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(query: String, apiKey: String) =
        repository.searchMovies(query, apiKey)
}