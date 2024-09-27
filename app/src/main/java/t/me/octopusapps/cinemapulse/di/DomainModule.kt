package t.me.octopusapps.cinemapulse.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import t.me.octopusapps.domain.repositories.MovieRepository
import t.me.octopusapps.domain.usecases.GetMovieDetailsUseCase
import t.me.octopusapps.domain.usecases.GetPopularMoviesUseCase
import t.me.octopusapps.domain.usecases.SearchMoviesUseCase

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    fun provideSearchMoviesUseCase(repository: MovieRepository) =
        SearchMoviesUseCase(repository)

    @Provides
    fun provideGetMovieDetailsUseCase(repository: MovieRepository) =
        GetMovieDetailsUseCase(repository)

    @Provides
    fun provideGetPopularMoviesUseCase(repository: MovieRepository) =
        GetPopularMoviesUseCase(repository)

}