package t.me.octopusapps.cinemapulse.presentation.models

internal data class MovieUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val popularity: Double,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val posterPath: String?,
    val backdropPath: String?,
    val genreIds: List<Int>?,
    val adult: Boolean,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean
)