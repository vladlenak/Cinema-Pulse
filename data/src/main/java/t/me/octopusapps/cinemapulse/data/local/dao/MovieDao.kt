package t.me.octopusapps.cinemapulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import t.me.octopusapps.cinemapulse.data.local.entities.MovieEntity

@Dao
internal interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM popular_movies WHERE page = :page ORDER BY id ASC")
    suspend fun getMoviesByPage(page: Int): List<MovieEntity>

    @Query("SELECT * FROM popular_movies WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query("DELETE FROM popular_movies")
    suspend fun clearAll()
}