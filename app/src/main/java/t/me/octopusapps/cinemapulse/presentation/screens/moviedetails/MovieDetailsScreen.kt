package t.me.octopusapps.cinemapulse.presentation.screens.moviedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import t.me.octopusapps.cinemapulse.R
import t.me.octopusapps.cinemapulse.presentation.components.StateMessageComponent
import t.me.octopusapps.cinemapulse.presentation.config.ImageConstants
import t.me.octopusapps.cinemapulse.presentation.config.genreNameRes
import t.me.octopusapps.cinemapulse.presentation.models.MovieUiModel
import t.me.octopusapps.cinemapulse.presentation.text.asString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieDetailsScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val detailsTitle = stringResource(R.string.movie_details_title)

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId)
    }

    when (val state = uiState) {
        is MovieDetailsUiState.Loading -> {
            MovieDetailsTransientScaffold(
                title = detailsTitle,
                onBackClick = onBackClick,
            ) {
                CircularProgressIndicator()
            }
        }

        is MovieDetailsUiState.Error -> {
            MovieDetailsTransientScaffold(
                title = detailsTitle,
                onBackClick = onBackClick,
            ) {
                StateMessageComponent(
                    icon = Icons.Default.Warning,
                    title = stringResource(R.string.movie_details_load_error_title),
                    message = state.message.asString(),
                    actionLabel = stringResource(R.string.common_try_again),
                    onActionClick = { viewModel.fetchMovieDetails(movieId) },
                    isError = true,
                )
            }
        }

        is MovieDetailsUiState.Success -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
            ) { innerPadding ->
                MovieDetailsContent(
                    movie = state.movie,
                    isFavorite = state.isFavorite,
                    isFavoriteUpdating = state.isFavoriteUpdating,
                    isWatched = state.isWatched,
                    isWatchedUpdating = state.isWatchedUpdating,
                    onBackClick = onBackClick,
                    onFavoriteClick = viewModel::onFavoriteClick,
                    onWatchedClick = viewModel::onWatchedClick,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieDetailsTransientScaffold(
    title: String,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MovieDetailsContent(
    movie: MovieUiModel,
    isFavorite: Boolean,
    isFavoriteUpdating: Boolean,
    isWatched: Boolean,
    isWatchedUpdating: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        MovieHero(
            movie = movie,
            isFavorite = isFavorite,
            isFavoriteUpdating = isFavoriteUpdating,
            isWatched = isWatched,
            isWatchedUpdating = isWatchedUpdating,
            onBackClick = onBackClick,
            onFavoriteClick = onFavoriteClick,
            onWatchedClick = onWatchedClick,
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (movie.overview.isNotBlank()) {
                DetailsSection(title = stringResource(R.string.movie_details_overview_section)) {
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            DetailsSection(title = stringResource(R.string.movie_details_details_section)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            label = stringResource(R.string.movie_details_rating_label),
                            value = stringResource(
                                R.string.movie_details_rating_value,
                                movie.voteAverage,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            label = stringResource(R.string.movie_details_votes_label),
                            value = formatVoteCount(movie.voteCount),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            label = stringResource(R.string.movie_details_release_label),
                            value = movie.releaseDate.ifBlank {
                                stringResource(R.string.common_tba)
                            },
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            label = stringResource(R.string.movie_details_language_label),
                            value = movie.originalLanguage.uppercase().ifBlank {
                                stringResource(R.string.common_not_available)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            val genreResourceIds = movie.genreIds?.mapNotNull { genreNameRes(it) } ?: emptyList()
            if (genreResourceIds.isNotEmpty()) {
                DetailsSection(title = stringResource(R.string.movie_details_genres_section)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        genreResourceIds.forEach { genreRes ->
                            GenreChip(text = stringResource(genreRes))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MovieHero(
    movie: MovieUiModel,
    isFavorite: Boolean,
    isFavoriteUpdating: Boolean,
    isWatched: Boolean,
    isWatchedUpdating: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchedClick: () -> Unit,
) {
    val backContentDescription = stringResource(R.string.common_back)
    val watchedContentDescription = if (isWatched) {
        stringResource(R.string.movie_details_remove_watched)
    } else {
        stringResource(R.string.movie_details_mark_watched)
    }
    val favoriteContentDescription = if (isFavorite) {
        stringResource(R.string.movie_details_remove_favorite)
    } else {
        stringResource(R.string.movie_details_add_favorite)
    }
    val unknownYear = stringResource(R.string.common_tba)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        HeroImage(movie = movie)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.60f),
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.78f),
                        ),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 12.dp, top = 8.dp, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeroIconButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backContentDescription,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroIconButton(
                    onClick = onWatchedClick,
                    icon = if (isWatched) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = watchedContentDescription,
                    enabled = !isWatchedUpdating,
                    selected = isWatched,
                )
                HeroIconButton(
                    onClick = onFavoriteClick,
                    icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = favoriteContentDescription,
                    enabled = !isFavoriteUpdating,
                    selected = isFavorite,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            PosterThumbnail(movie = movie)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (movie.adult) {
                    AdultBadge()
                }

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                if (movie.originalTitle.isNotBlank() && movie.originalTitle != movie.title) {
                    Text(
                        text = movie.originalTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.76f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RatingPill(voteAverage = movie.voteAverage)
                    MetaPill(text = movie.releaseYear(unknownYear))
                    if (movie.originalLanguage.isNotBlank()) {
                        MetaPill(text = movie.originalLanguage.uppercase())
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroImage(movie: MovieUiModel) {
    val imagePath = movie.backdropPath ?: movie.posterPath

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (imagePath != null) {
            AsyncImage(
                model = "${ImageConstants.IMAGE_BASE_URL}$imagePath",
                contentDescription = stringResource(
                    R.string.content_description_movie_backdrop,
                    movie.title,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

@Composable
private fun PosterThumbnail(movie: MovieUiModel) {
    Card(
        modifier = Modifier
            .width(104.dp)
            .aspectRatio(2f / 3f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        if (movie.posterPath != null) {
            AsyncImage(
                model = "${ImageConstants.IMAGE_BASE_URL}${movie.posterPath}",
                contentDescription = stringResource(
                    R.string.content_description_movie_poster,
                    movie.title,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun HeroIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    Surface(
        shape = CircleShape,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.94f)
        } else {
            Color.Black.copy(alpha = 0.42f)
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            Color.White
        },
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(44.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
private fun DetailsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RatingPill(voteAverage: Double) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = String.format("%.1f", voteAverage),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun MetaPill(text: String) {
    Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.46f),
        contentColor = Color.White,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun AdultBadge() {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Text(
            text = stringResource(R.string.common_adult_badge),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun GenreChip(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}

private const val RELEASE_YEAR_LENGTH = 4

private fun MovieUiModel.releaseYear(fallback: String): String =
    releaseDate.take(RELEASE_YEAR_LENGTH).takeIf {
        it.length == RELEASE_YEAR_LENGTH
    } ?: fallback

@Composable
private fun formatVoteCount(count: Int): String = when {
    count >= 1_000_000 -> stringResource(
        R.string.movie_details_vote_count_millions,
        count / 1_000_000.0,
    )

    count >= 1_000 -> stringResource(
        R.string.movie_details_vote_count_thousands,
        count / 1_000.0,
    )

    else -> count.toString()
}
