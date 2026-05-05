package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.repositories.MovieRepository

public class IsMovieFavoriteUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(movieId: Int): Boolean =
        repository.isMovieFavorite(movieId)
}
