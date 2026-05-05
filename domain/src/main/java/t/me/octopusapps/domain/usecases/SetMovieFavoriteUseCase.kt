package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

public class SetMovieFavoriteUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(movie: Movie, isFavorite: Boolean) {
        if (isFavorite) {
            repository.addFavoriteMovie(movie)
        } else {
            repository.removeFavoriteMovie(movie.id)
        }
    }
}
