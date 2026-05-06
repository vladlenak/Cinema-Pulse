package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

public class GetFavoriteMoviesUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(): List<Movie> = repository.getFavoriteMovies()
}
