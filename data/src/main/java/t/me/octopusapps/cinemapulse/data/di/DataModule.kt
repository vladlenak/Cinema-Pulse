package t.me.octopusapps.cinemapulse.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import t.me.octopusapps.cinemapulse.data.BuildConfig
import t.me.octopusapps.cinemapulse.data.config.ApiConstants
import t.me.octopusapps.cinemapulse.data.local.CinemaPulseDatabase
import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.remote.AuthInterceptor
import t.me.octopusapps.cinemapulse.data.remote.MovieApi
import t.me.octopusapps.cinemapulse.data.repositories.MovieRepositoryImpl
import t.me.octopusapps.domain.repositories.MovieRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CinemaPulseDatabase =
        Room.databaseBuilder(
            context,
            CinemaPulseDatabase::class.java,
            "cinema_pulse.db"
        )
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
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
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApi(okHttpClient: OkHttpClient): MovieApi =
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApi::class.java)

    @Provides
    @Singleton
    fun provideMovieRepository(api: MovieApi, movieDao: MovieDao): MovieRepository =
        MovieRepositoryImpl(api, movieDao)

    private val MIGRATION_2_3 = Migration(2, 3) { database ->
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `favorite_movies` (
                `id` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `overview` TEXT NOT NULL,
                `popularity` REAL NOT NULL,
                `releaseDate` TEXT NOT NULL,
                `voteAverage` REAL NOT NULL,
                `voteCount` INTEGER NOT NULL,
                `posterPath` TEXT,
                `backdropPath` TEXT,
                `genreIds` TEXT,
                `adult` INTEGER NOT NULL,
                `originalLanguage` TEXT NOT NULL,
                `originalTitle` TEXT NOT NULL,
                `video` INTEGER NOT NULL,
                `addedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }

    private val MIGRATION_3_4 = Migration(3, 4) { database ->
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `watched_movies` (
                `id` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `overview` TEXT NOT NULL,
                `popularity` REAL NOT NULL,
                `releaseDate` TEXT NOT NULL,
                `voteAverage` REAL NOT NULL,
                `voteCount` INTEGER NOT NULL,
                `posterPath` TEXT,
                `backdropPath` TEXT,
                `genreIds` TEXT,
                `adult` INTEGER NOT NULL,
                `originalLanguage` TEXT NOT NULL,
                `originalTitle` TEXT NOT NULL,
                `video` INTEGER NOT NULL,
                `watchedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}
