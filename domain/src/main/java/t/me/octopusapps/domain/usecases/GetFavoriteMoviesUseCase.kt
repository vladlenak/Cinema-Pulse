package t.me.octopusapps.domain.usecases

import kotlinx.coroutines.flow.Flow
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

public class GetFavoriteMoviesUseCase(private val repository: MovieRepository) {
    public operator fun invoke(): Flow<List<Movie>> = repository.getFavoriteMovies()
}
