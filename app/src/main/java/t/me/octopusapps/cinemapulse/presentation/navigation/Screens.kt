package t.me.octopusapps.cinemapulse.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object MovieList

@Serializable
data class MovieDetails(val movieId: Int)

@Serializable
object MovieSearch