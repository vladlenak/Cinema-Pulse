package t.me.octopusapps.cinemapulse.presentation.screens.moviesearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import t.me.octopusapps.cinemapulse.presentation.components.MovieItemComponent
import t.me.octopusapps.cinemapulse.presentation.components.StateMessageComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieSearchScreen(
    onMovieClick: (Int) -> Unit,
    onBackClick: (() -> Unit)? = null,
    viewModel: MovieSearchViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Movies") },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    query = newQuery
                    viewModel.onQueryChanged(newQuery)
                },
                label = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                query = ""
                                viewModel.onQueryChanged("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is MovieSearchUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is MovieSearchUiState.Success -> {
                        when {
                            query.isBlank() -> {
                                StateMessageComponent(
                                    modifier = Modifier.fillMaxSize(),
                                    icon = Icons.Default.Search,
                                    title = "Search the catalog",
                                    message = "Find movies by title and open details from the results."
                                )
                            }

                            state.movies.isEmpty() -> {
                                StateMessageComponent(
                                    modifier = Modifier.fillMaxSize(),
                                    icon = Icons.Default.Search,
                                    title = "No results",
                                    message = "No movies found for \"$query\".",
                                    actionLabel = "Clear search",
                                    onActionClick = {
                                        query = ""
                                        viewModel.onQueryChanged("")
                                    }
                                )
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 24.dp)
                                ) {
                                    items(
                                        items = state.movies,
                                        key = { movie -> movie.id }
                                    ) { movie ->
                                        MovieItemComponent(
                                            movie = movie,
                                            modifier = Modifier.animateItem()
                                        ) {
                                            onMovieClick(movie.id)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is MovieSearchUiState.Error -> {
                        StateMessageComponent(
                            modifier = Modifier.fillMaxSize(),
                            icon = Icons.Default.Warning,
                            title = "Search failed",
                            message = state.message,
                            actionLabel = "Try again",
                            onActionClick = viewModel::retry,
                            isError = true
                        )
                    }
                }
            }
        }
    }
}
