package t.me.octopusapps.cinemapulse.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import t.me.octopusapps.domain.repositories.MovieRepository
import t.me.octopusapps.domain.usecases.GetFavoriteMoviesUseCase
import t.me.octopusapps.domain.usecases.GetMovieDetailsUseCase
import t.me.octopusapps.domain.usecases.GetMoviesByCategoryUseCase
import t.me.octopusapps.domain.usecases.GetWatchedMoviesUseCase
import t.me.octopusapps.domain.usecases.IsMovieFavoriteUseCase
import t.me.octopusapps.domain.usecases.IsMovieWatchedUseCase
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase
import t.me.octopusapps.domain.usecases.SetMovieFavoriteUseCase
import t.me.octopusapps.domain.usecases.SetMovieWatchedUseCase

@Module
@InstallIn(ViewModelComponent::class)
internal object DomainModule {

    @Provides
    fun provideSearchMoviesUseCase(repository: MovieRepository) =
        SearchMoviesUseCase(repository)

    @Provides
    fun provideGetMovieDetailsUseCase(repository: MovieRepository) =
        GetMovieDetailsUseCase(repository)

    @Provides
    fun provideGetMoviesByCategoryUseCase(repository: MovieRepository) =
        GetMoviesByCategoryUseCase(repository)

    @Provides
    fun provideGetFavoriteMoviesUseCase(repository: MovieRepository) =
        GetFavoriteMoviesUseCase(repository)

    @Provides
    fun provideIsMovieFavoriteUseCase(repository: MovieRepository) =
        IsMovieFavoriteUseCase(repository)

    @Provides
    fun provideSetMovieFavoriteUseCase(repository: MovieRepository) =
        SetMovieFavoriteUseCase(repository)

    @Provides
    fun provideGetWatchedMoviesUseCase(repository: MovieRepository) =
        GetWatchedMoviesUseCase(repository)

    @Provides
    fun provideIsMovieWatchedUseCase(repository: MovieRepository) =
        IsMovieWatchedUseCase(repository)

    @Provides
    fun provideSetMovieWatchedUseCase(repository: MovieRepository) =
        SetMovieWatchedUseCase(repository)
}
