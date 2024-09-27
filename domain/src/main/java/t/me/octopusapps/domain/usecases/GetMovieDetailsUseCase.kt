package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.repositories.MovieRepository

class GetMovieDetailsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int, apiKey: String) =
        repository.getMovieDetails(movieId, apiKey)
}