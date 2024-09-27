package t.me.octopusapps.cinemapulse.data.mapper

import t.me.octopusapps.cinemapulse.data.models.MovieDetails
import t.me.octopusapps.cinemapulse.data.models.MovieResponse
import t.me.octopusapps.domain.model.Movie
import t.me.octopusapps.domain.model.MovieList

fun MovieDetails.mapToMovie() =
    Movie(
        id = this.id,
        title = this.title,
        overview = this.overview,
        popularity = this.popularity,
        releaseDate = this.releaseDate,
        voteAverage = this.voteAverage,
        voteCount = this.voteCount,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        genreIds = this.genreIds,
        adult = this.adult,
        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle,
        video = this.video
    )

fun MovieResponse.mapToMovieList() =
    MovieList(
        page = this.page,
        results = this.results.map { it.mapToMovie() }
    )