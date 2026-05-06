package t.me.octopusapps.cinemapulse.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import t.me.octopusapps.cinemapulse.data.BuildConfig
import t.me.octopusapps.cinemapulse.data.config.ApiConstants
import t.me.octopusapps.cinemapulse.data.local.CinemaPulseDatabase
import t.me.octopusapps.cinemapulse.data.local.CinemaPulseMigrations
import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.remote.AuthInterceptor
import t.me.octopusapps.cinemapulse.data.remote.HttpLoggingInterceptorFactory
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.cinemapulse.data.repositories.MovieRepositoryImpl
import t.me.octopusapps.domain.repositories.MovieRepository

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    private const val NETWORK_TIMEOUT_SECONDS = 15L

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CinemaPulseDatabase =
        Room.databaseBuilder(
            context,
            CinemaPulseDatabase::class.java,
            "cinema_pulse.db",
        )
            .addMigrations(*CinemaPulseMigrations.ALL)
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideMovieDao(db: CinemaPulseDatabase): MovieDao = db.movieDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val apiKey = BuildConfig.TMDB_API_KEY
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKey))
            .addInterceptor(HttpLoggingInterceptorFactory.create(isDebug = BuildConfig.DEBUG))
            .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApi(okHttpClient: OkHttpClient): MovieApi = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieApi::class.java)

    @Provides
    @Singleton
    fun provideMovieRepository(api: MovieApi, movieDao: MovieDao): MovieRepository =
        MovieRepositoryImpl(api, movieDao)
}
