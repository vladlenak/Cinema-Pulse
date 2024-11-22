package t.me.octopusapps.cinemapulse.data.models

import com.google.gson.annotations.SerializedName

internal data class MovieResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<MovieDetails>
)