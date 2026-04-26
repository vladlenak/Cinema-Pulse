package t.me.octopusapps.cinemapulse.data.local.mapper

import t.me.octopusapps.cinemapulse.data.local.entities.MovieEntity
import t.me.octopusapps.domain.models.Movie
import t.me.octopusapps.domain.models.MovieCategory

internal fun Movie.toEntity(category: MovieCategory, page: Int, totalPages: Int): MovieEntity =
    MovieEntity(
        id = id,
        category = category.name,
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