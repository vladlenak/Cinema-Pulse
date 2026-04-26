package t.me.octopusapps.domain.models

public enum class MovieCategory(public val label: String) {
    POPULAR("Popular"),
    TOP_RATED("Top Rated"),
    UPCOMING("Upcoming"),
    NOW_PLAYING("Now Playing")
}