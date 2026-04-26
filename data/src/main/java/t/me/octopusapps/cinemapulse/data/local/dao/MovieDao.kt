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

    @Query("SELECT * FROM movies WHERE category = :category AND page = :page ORDER BY rowId ASC")
    suspend fun getMoviesByCategoryAndPage(category: String, page: Int): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query("DELETE FROM movies WHERE category = :category")
    suspend fun clearByCategory(category: String)

    @Query("DELETE FROM movies")
    suspend fun clearAll()
}