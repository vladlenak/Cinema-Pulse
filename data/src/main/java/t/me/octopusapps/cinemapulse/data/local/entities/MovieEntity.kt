package t.me.octopusapps.cinemapulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
internal data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Int = 0,
    val id: Int,
    val category: String,
    val title: String,
    val overview: String,
    val popularity: Double,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val posterPath: String?,
    val backdropPath: String?,
    val genreIds: String?,
    val adult: Boolean,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean,
    val page: Int,
    val totalPages: Int,
    val cachedAt: Long = System.currentTimeMillis()
)