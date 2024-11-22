package t.me.octopusapps.cinemapulse.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
internal object MovieList

@Serializable
internal data class MovieDetails(val movieId: Int)

@Serializable
internal object MovieSearch