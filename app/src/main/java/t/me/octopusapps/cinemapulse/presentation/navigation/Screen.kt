package t.me.octopusapps.cinemapulse.presentation.navigation

sealed class Screen(val route: String) {
    data object MovieList : Screen("movie_list")
    data object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: Int) = "movie_details/$movieId"
    }

    data object MovieSearch : Screen("movie_search")
}