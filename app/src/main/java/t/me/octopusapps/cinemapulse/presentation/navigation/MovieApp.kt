package t.me.octopusapps.cinemapulse.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import t.me.octopusapps.cinemapulse.R
import t.me.octopusapps.cinemapulse.presentation.screens.favorites.FavoriteMoviesScreen
import t.me.octopusapps.cinemapulse.presentation.screens.moviedetails.MovieDetailsScreen
import t.me.octopusapps.cinemapulse.presentation.screens.movielist.MovieListScreen
import t.me.octopusapps.cinemapulse.presentation.screens.moviesearch.MovieSearchScreen
import t.me.octopusapps.cinemapulse.presentation.screens.watched.WatchedMoviesScreen

@Composable
internal fun MovieApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentDestination == null || currentDestination.isTopLevelDestination()) {
                CinemaPulseNavigationBar(
                    currentDestination = currentDestination,
                    onDestinationClick = { destination ->
                        navController.navigateToTopLevelDestination(destination)
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieList,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<MovieList> {
                MovieListScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetails(movieId = movieId))
                    },
                )
            }
            composable<MovieSearch> {
                MovieSearchScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetails(movieId = movieId))
                    },
                )
            }
            composable<FavoriteMovies> {
                FavoriteMoviesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetails(movieId = movieId))
                    },
                    onBrowseMoviesClick = {
                        navController.navigateToTopLevelDestination(TopLevelDestination.Movies)
                    },
                )
            }
            composable<WatchedMovies> {
                WatchedMoviesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetails(movieId = movieId))
                    },
                    onBrowseMoviesClick = {
                        navController.navigateToTopLevelDestination(TopLevelDestination.Movies)
                    },
                )
            }
            composable<MovieDetails> { movieDetailsEntry ->
                val movieId = movieDetailsEntry.toRoute<MovieDetails>()
                MovieDetailsScreen(
                    movieId = movieId.movieId,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun CinemaPulseNavigationBar(
    currentDestination: NavDestination?,
    onDestinationClick: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val label = stringResource(destination.labelRes)
            NavigationBarItem(
                selected = currentDestination.isSelected(destination),
                onClick = { onDestinationClick(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = label,
                    )
                },
                label = { Text(label) },
            )
        }
    }
}

private enum class TopLevelDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val route: Any,
    val routeName: String,
) {
    Movies(
        labelRes = R.string.nav_movies,
        icon = Icons.Default.Home,
        route = MovieList,
        routeName = MovieList::class.qualifiedName.orEmpty(),
    ),
    Search(
        labelRes = R.string.nav_search,
        icon = Icons.Default.Search,
        route = MovieSearch,
        routeName = MovieSearch::class.qualifiedName.orEmpty(),
    ),
    Favorites(
        labelRes = R.string.nav_favorites,
        icon = Icons.Default.Favorite,
        route = FavoriteMovies,
        routeName = FavoriteMovies::class.qualifiedName.orEmpty(),
    ),
    Watched(
        labelRes = R.string.nav_watched,
        icon = Icons.Default.Visibility,
        route = WatchedMovies,
        routeName = WatchedMovies::class.qualifiedName.orEmpty(),
    ),
}

private fun NavDestination?.isTopLevelDestination(): Boolean = TopLevelDestination.entries.any {
    isSelected(it)
}

private fun NavDestination?.isSelected(destination: TopLevelDestination): Boolean =
    this?.route == destination.routeName

private fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
