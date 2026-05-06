package t.me.octopusapps.cinemapulse.presentation.config

import androidx.annotation.StringRes
import t.me.octopusapps.cinemapulse.R
import t.me.octopusapps.domain.models.MovieCategory

@get:StringRes
internal val MovieCategory.labelRes: Int
    get() = when (this) {
        MovieCategory.POPULAR -> R.string.category_popular
        MovieCategory.TOP_RATED -> R.string.category_top_rated
        MovieCategory.UPCOMING -> R.string.category_upcoming
        MovieCategory.NOW_PLAYING -> R.string.category_now_playing
    }
