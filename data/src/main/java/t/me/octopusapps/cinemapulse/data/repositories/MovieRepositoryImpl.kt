package t.me.octopusapps.cinemapulse.data.repositories

import android.database.SQLException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.local.mapper.toDomain
import t.me.octopusapps.cinemapulse.data.local.mapper.toEntity
import t.me.octopusapps.cinemapulse.data.local.mapper.toFavoriteEntity
import t.me.octopusapps.cinemapulse.data.local.mapper.toWatchedEntity
import t.me.octopusapps.cinemapulse.data.mapper.mapToMovie
import t.me.octopusapps.cinemapulse.data.mapper.mapToMovieList
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.domain.errors.MovieError
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory
import t.me.octopusapps.domain.models.MovieList
import t.me.octopusapps.domain.repositories.MovieRepository
import java.io.IOException

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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val cached = runStorageRequest {
                movieDao.getMoviesByCategoryAndPage(category.name, page)
            }
            if (cached.isNotEmpty()) {
                MovieList(
                    page = page,
                    results = cached.map { it.toDomain() },
                    totalPages = cached.first().totalPages
                )
            } else {
                throw e.toMovieError()
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            runStorageRequest {
                movieDao.getMovieById(movieId)
            }?.toDomain() ?: throw e.toMovieError(movieId = movieId)
        }
    }

    override suspend fun searchMovies(query: String): MovieList =
        runRemoteRequest {
            api.searchMovies(query).mapToMovieList()
        }

    override suspend fun getFavoriteMovies(): List<Movie> =
        runStorageRequest {
            movieDao.getFavoriteMovies().map { it.toDomain() }
        }

    override suspend fun isMovieFavorite(movieId: Int): Boolean =
        runStorageRequest {
            movieDao.isMovieFavorite(movieId)
        }

    override suspend fun addFavoriteMovie(movie: Movie) {
        runStorageRequest {
            movieDao.insertFavoriteMovie(movie.toFavoriteEntity())
        }
    }

    override suspend fun removeFavoriteMovie(movieId: Int) {
        runStorageRequest {
            movieDao.deleteFavoriteMovie(movieId)
        }
    }

    override suspend fun getWatchedMovies(): List<Movie> =
        runStorageRequest {
            movieDao.getWatchedMovies().map { it.toDomain() }
        }

    override suspend fun isMovieWatched(movieId: Int): Boolean =
        runStorageRequest {
            movieDao.isMovieWatched(movieId)
        }

    override suspend fun addWatchedMovie(movie: Movie) {
        runStorageRequest {
            movieDao.insertWatchedMovie(movie.toWatchedEntity())
        }
    }

    override suspend fun removeWatchedMovie(movieId: Int) {
        runStorageRequest {
            movieDao.deleteWatchedMovie(movieId)
        }
    }

    private suspend fun <T> runRemoteRequest(block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e.toMovieError()
        }

    private suspend fun <T> runStorageRequest(block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: MovieError) {
            throw e
        } catch (e: Exception) {
            throw MovieError.Storage(e)
        }

    private fun Exception.toMovieError(movieId: Int? = null): MovieError =
        when (this) {
            is MovieError -> this
            is IOException -> MovieError.Network(this)
            is HttpException -> {
                if (code() == HTTP_NOT_FOUND) {
                    MovieError.NotFound(movieId = movieId, cause = this)
                } else {
                    MovieError.Remote(code = code(), cause = this)
                }
            }
            is SQLException -> MovieError.Storage(this)
            else -> MovieError.Unknown(this)
        }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
