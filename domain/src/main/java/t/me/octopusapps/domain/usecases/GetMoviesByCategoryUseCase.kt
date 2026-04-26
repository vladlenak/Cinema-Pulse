package t.me.octopusapps.domain.usecases

import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

public class GetMoviesByCategoryUseCase(private val repository: MovieRepository) {
    public suspend operator fun invoke(category: MovieCategory, page: Int): MovieList =
        repository.getMoviesByCategory(category, page)
}