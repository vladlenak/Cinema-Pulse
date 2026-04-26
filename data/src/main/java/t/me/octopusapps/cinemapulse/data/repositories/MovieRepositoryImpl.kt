package t.me.octopusapps.cinemapulse.data.repositories

import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.local.mapper.toDomain
import t.me.octopusapps.cinemapulse.data.local.mapper.toEntity
import t.me.octopusapps.cinemapulse.data.mapper.mapToMovie
import t.me.octopusapps.cinemapulse.data.mapper.mapToMovieList
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository

internal class MovieRepositoryImpl(
    private val api: MovieApi,
    private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): MovieList =
        getMoviesByCategory(MovieCategory.POPULAR, page)

    override suspend fun getMoviesByCategory(category: MovieCategory, page: Int): MovieList {
        return try {
            val response = when (category) {
                MovieCategory.POPULAR -> api.getPopularMovies(page = page)
                MovieCategory.TOP_RATED -> api.getTopRatedMovies(page = page)
                MovieCategory.UPCOMING -> api.getUpcomingMovies(page = page)
                MovieCategory.NOW_PLAYING -> api.getNowPlayingMovies(page = page)
            }
            val movieList = response.mapToMovieList()
            movieDao.insertMovies(
                movieList.results.map { it.toEntity(category, page, movieList.totalPages) }
            )
            movieList
        } catch (e: Exception) {
            val cached = movieDao.getMoviesByCategoryAndPage(category.name, page)
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
            movieDao.insertMovie(
                movie.toEntity(MovieCategory.POPULAR, page = 0, totalPages = 0)
            )
            movie
        } catch (e: Exception) {
            movieDao.getMovieById(movieId)?.toDomain() ?: throw e
        }
    }

    override suspend fun searchMovies(query: String): MovieList =
        api.searchMovies(query).mapToMovieList()
}