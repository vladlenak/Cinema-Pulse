package t.me.octopusapps.domain.repositories

import kotlinx.coroutines.flow.Flow
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.models.MovieList

public interface MovieRepository {
    public suspend fun getPopularMovies(page: Int): MovieList
    public suspend fun getMoviesByCategory(category: MovieCategory, page: Int): MovieList
    public suspend fun getMovieDetails(movieId: Int): Movie
    public suspend fun searchMovies(query: String): MovieList
    public fun getFavoriteMovies(): Flow<List<Movie>>
    public suspend fun isMovieFavorite(movieId: Int): Boolean
    public suspend fun addFavoriteMovie(movie: Movie)
    public suspend fun removeFavoriteMovie(movieId: Int)
    public fun getWatchedMovies(): Flow<List<Movie>>
    public suspend fun isMovieWatched(movieId: Int): Boolean
    public suspend fun addWatchedMovie(movie: Movie)
    public suspend fun removeWatchedMovie(movieId: Int)
}
