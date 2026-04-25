package t.me.octopusapps.cinemapulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular_movies")
internal data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val popularity: Double,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val posterPath: String?,
    val backdropPath: String?,
    val genreIds: String?,         // stored as "28,878" — converted via Converters
    val adult: Boolean,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean,
    val page: Int,                 // which pagination page this movie belongs to
    val totalPages: Int,           // total pages at time of caching
    val cachedAt: Long = System.currentTimeMillis()
)