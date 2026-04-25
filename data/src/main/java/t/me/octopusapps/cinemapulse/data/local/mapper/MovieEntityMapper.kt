package t.me.octopusapps.cinemapulse.data.local.mapper

import t.me.octopusapps.cinemapulse.data.local.entities.MovieEntity
import t.me.octopusapps.domain.models.Movie

internal fun Movie.toEntity(page: Int, totalPages: Int): MovieEntity =
    MovieEntity(
        id = id,
        title = title,
        overview = overview,
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        posterPath = posterPath,
        backdropPath = backdropPath,
        genreIds = genreIds?.joinToString(","),
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        video = video,
        page = page,
        totalPages = totalPages
    )

internal fun MovieEntity.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        overview = overview,
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        posterPath = posterPath,
        backdropPath = backdropPath,
        genreIds = genreIds?.split(",")?.mapNotNull { it.toIntOrNull() },
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        video = video
    )