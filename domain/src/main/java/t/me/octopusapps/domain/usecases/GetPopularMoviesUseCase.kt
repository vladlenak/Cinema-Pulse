package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.repositories.MovieRepository

class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(apiKey: String) = repository.getPopularMovies(apiKey)
}