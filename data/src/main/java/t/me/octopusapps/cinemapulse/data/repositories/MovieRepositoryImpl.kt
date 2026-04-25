package t.me.octopusapps.cinemapulse.data.repositories

import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.local.mapper.toDomain
import t.me.octopusapps.cinemapulse.data.local.mapper.toEntity
import t.me.octopusapps.cinemapulse.data.mapper.mapToMovie
import t.me.octopusapps.cinemapulse.data.mapper.mapToMovieList
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

internal class MovieRepositoryImpl(
    private val api: MovieApi,
    private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): MovieList {
        return try {
            val movieList = api.getPopularMovies(page = page).mapToMovieList()
            movieDao.insertMovies(
                movieList.results.map { it.toEntity(page, movieList.totalPages) }
            )
            movieList
        } catch (e: Exception) {
            val cached = movieDao.getMoviesByPage(page)
            if (cached.isNotEmpty()) {
                MovieList(
                    page = page,
                    results = cached.map { it.toDomain() },
                    totalPages = cached.first().totalPages
                )
            } else {
                throw e
            }
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Movie {
        return try {
            val movie = api.getMovieDetails(movieId).mapToMovie()
            movieDao.insertMovie(movie.toEntity(page = 0, totalPages = 0))
            movie
        } catch (e: Exception) {
            movieDao.getMovieById(movieId)?.toDomain() ?: throw e
        }
    }

    override suspend fun searchMovies(query: String): MovieList {
        return api.searchMovies(query).mapToMovieList()
    }
}