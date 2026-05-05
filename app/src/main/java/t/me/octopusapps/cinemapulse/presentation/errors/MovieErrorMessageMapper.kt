package t.me.octopusapps.cinemapulse.presentation.errors

import t.me.octopusapps.domain.errors.MovieError

internal fun Throwable.toMovieErrorMessage(
    defaultMessage: String = "An unexpected error occurred"
): String = when (this) {
    is MovieError -> userMessage
    else -> message ?: defaultMessage
}
