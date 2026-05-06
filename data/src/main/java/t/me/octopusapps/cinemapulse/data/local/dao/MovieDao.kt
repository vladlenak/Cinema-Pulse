package t.me.octopusapps.cinemapulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import t.me.octopusapps.cinemapulse.data.local.entities.FavoriteMovieEntity
import t.me.octopusapps.cinemapulse.data.local.entities.MovieDetailsEntity
import t.me.octopusapps.cinemapulse.data.local.entities.MovieEntity
import t.me.octopusapps.cinemapulse.data.local.entities.WatchedMovieEntity

@Dao
internal interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(movie: MovieDetailsEntity)

    @Query("SELECT * FROM movies WHERE category = :category AND page = :page ORDER BY rowId ASC")
    suspend fun getMoviesByCategoryAndPage(category: String, page: Int): List<MovieEntity>

    @Query("SELECT * FROM movie_details WHERE id = :id")
    suspend fun getMovieDetailsById(id: Int): MovieDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(movie: FavoriteMovieEntity)

    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    suspend fun getFavoriteMovies(): List<FavoriteMovieEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    suspend fun isMovieFavorite(movieId: Int): Boolean

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteMovie(movieId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchedMovie(movie: WatchedMovieEntity)

    @Query("SELECT * FROM watched_movies ORDER BY watchedAt DESC")
    suspend fun getWatchedMovies(): List<WatchedMovieEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM watched_movies WHERE id = :movieId)")
    suspend fun isMovieWatched(movieId: Int): Boolean

    @Query("DELETE FROM watched_movies WHERE id = :movieId")
    suspend fun deleteWatchedMovie(movieId: Int)

    @Query("DELETE FROM movies WHERE category = :category")
    suspend fun clearByCategory(category: String)

    @Query("DELETE FROM movies")
    suspend fun clearAll()
}
