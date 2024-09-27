package t.me.octopusapps.cinemapulse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import t.me.octopusapps.cinemapulse.presentation.screens.moviedetails.MovieDetailsScreen
import t.me.octopusapps.cinemapulse.presentation.screens.movielist.MovieListScreen
import t.me.octopusapps.cinemapulse.presentation.screens.moviesearch.MovieSearchScreen

@Composable
fun MovieApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MovieList.route) {
        composable(Screen.MovieList.route) {
            MovieListScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                },
                onSearchClick = {
                    navController.navigate(Screen.MovieSearch.route)
                }
            )
        }
        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            MovieDetailsScreen(movieId)
        }
        composable(Screen.MovieSearch.route) {
            MovieSearchScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                }
            )
        }
    }
}