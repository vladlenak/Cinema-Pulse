package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.repositories.MovieRepository

public class SetMovieWatchedUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(movie: Movie, isWatched: Boolean) {
        if (isWatched) {
            repository.addWatchedMovie(movie)
        } else {
            repository.removeWatchedMovie(movie.id)
        }
    }
}
