package t.me.octopusapps.cinemapulse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import t.me.octopusapps.cinemapulse.presentation.screens.moviedetails.MovieDetailsScreen
import t.me.octopusapps.cinemapulse.presentation.screens.movielist.MovieListScreen
import t.me.octopusapps.cinemapulse.presentation.screens.moviesearch.MovieSearchScreen

@Composable
internal fun MovieApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MovieList) {
        composable<MovieList> {
            MovieListScreen(
                onMovieClick = { movieId ->
                    navController.navigate(MovieDetails(movieId = movieId))
                },
                onSearchClick = {
                    navController.navigate(MovieSearch)
                }
            )
        }
        composable<MovieSearch> {
            MovieSearchScreen(
                onMovieClick = { movieId ->
                    navController.navigate(MovieDetails(movieId = movieId))
                }
            )
        }
        composable<MovieDetails> { backStackEntry ->
            val movieId = backStackEntry.toRoute<MovieDetails>()
            MovieDetailsScreen(movieId.movieId)
        }
    }
}