package t.me.octopusapps.cinemapulse.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import t.me.octopusapps.cinemapulse.data.BuildConfig
import t.me.octopusapps.cinemapulse.data.config.ApiConstants
import t.me.octopusapps.cinemapulse.data.remote.AuthInterceptor
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.cinemapulse.data.repositories.MovieRepositoryImpl
import t.me.octopusapps.domain.repositories.MovieRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val apiKey = BuildConfig.TMDB_API_KEY
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKey))
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApi(okHttpClient: OkHttpClient): MovieApi {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(api: MovieApi): MovieRepository {
        return MovieRepositoryImpl(api)
    }

}