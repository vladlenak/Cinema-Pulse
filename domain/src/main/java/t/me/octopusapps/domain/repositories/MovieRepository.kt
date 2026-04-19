package t.me.octopusapps.domain.repositories

import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieList

public interface MovieRepository {
    public suspend fun getPopularMovies(): MovieList
    public suspend fun getMovieDetails(movieId: Int): Movie
    public suspend fun searchMovies(query: String): MovieList
}