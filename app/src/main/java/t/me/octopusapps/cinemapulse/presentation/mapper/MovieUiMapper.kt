package t.me.octopusapps.cinemapulse.presentation.mapper

import t.me.octopusapps.cinemapulse.presentation.models.MovieUiList
import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.domain.model.Movie
import t.me.octopusapps.domain.model.MovieList

fun Movie.mapToMovieUiModel() =
    MovieUiModel(
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

fun MovieList.mapToMovieUiList() =
    MovieUiList(
        page = this.page,
        results = this.results.map { it.mapToMovieUiModel() }
    )