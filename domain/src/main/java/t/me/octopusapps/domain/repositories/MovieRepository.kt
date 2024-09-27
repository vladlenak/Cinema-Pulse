package t.me.octopusapps.domain.repositories

import t.me.octopusapps.domain.model.Movie
import t.me.octopusapps.domain.model.MovieList

interface MovieRepository {
    suspend fun getPopularMovies(apiKey: String): MovieList
    suspend fun getMovieDetails(movieId: Int, apiKey: String): Movie
    suspend fun searchMovies(query: String, apiKey: String): MovieList
}