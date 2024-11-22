package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

public class GetMovieDetailsUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(movieId: Int, apiKey: String): Movie =
        repository.getMovieDetails(movieId, apiKey)
}