package t.me.octopusapps.cinemapulse.presentation.errors

import t.me.octopusapps.cinemapulse.R
import t.me.octopusapps.cinemapulse.presentation.text.UiText
import t.me.octopusapps.domain.errors.MovieError

internal fun Throwable.toMovieErrorMessage(
    defaultMessage: UiText = UiText.StringResource(R.string.error_unexpected)
): UiText = when (this) {
    is MovieError.Network -> UiText.StringResource(R.string.error_network)
    is MovieError.Remote -> UiText.StringResource(R.string.error_remote)
    is MovieError.NotFound -> UiText.StringResource(R.string.error_not_found)
    is MovieError.Storage -> UiText.StringResource(R.string.error_storage)
    is MovieError.Unknown -> UiText.StringResource(R.string.error_unexpected)
    else -> message
        ?.takeIf { it.isNotBlank() }
        ?.let(UiText::DynamicString)
        ?: defaultMessage
}
