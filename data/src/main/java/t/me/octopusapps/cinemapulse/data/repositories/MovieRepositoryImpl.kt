package t.me.octopusapps.cinemapulse.data.repositories

import t.me.octopusapps.cinemapulse.data.mapper.mapToMovie
import t.me.octopusapps.cinemapulse.data.mapper.mapToMovieList
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.domain.model.Movie
import t.me.octopusapps.domain.model.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

class MovieRepositoryImpl(private val api: MovieApi) : MovieRepository {

    override suspend fun getPopularMovies(apiKey: String): MovieList {
        return api.getPopularMovies(apiKey).mapToMovieList()
    }

    override suspend fun getMovieDetails(movieId: Int, apiKey: String): Movie {
        return api.getMovieDetails(movieId, apiKey).mapToMovie()
    }

    override suspend fun searchMovies(query: String, apiKey: String): MovieList {
        return api.searchMovies(apiKey, query).mapToMovieList()
    }

}