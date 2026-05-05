package t.me.octopusapps.domain.repositories

import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.models.MovieList

public interface MovieRepository {
    public suspend fun getPopularMovies(page: Int): MovieList
    public suspend fun getMoviesByCategory(category: MovieCategory, page: Int): MovieList
    public suspend fun getMovieDetails(movieId: Int): Movie
    public suspend fun searchMovies(query: String): MovieList
    public suspend fun getFavoriteMovies(): List<Movie>
    public suspend fun isMovieFavorite(movieId: Int): Boolean
    public suspend fun addFavoriteMovie(movie: Movie)
    public suspend fun removeFavoriteMovie(movieId: Int)
}
