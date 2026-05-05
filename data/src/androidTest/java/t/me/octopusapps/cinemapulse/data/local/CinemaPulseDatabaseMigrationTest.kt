package t.me.octopusapps.cinemapulse.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class CinemaPulseDatabaseMigrationTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migrate4To5_removesDuplicateCachedMoviesAndCreatesUniqueIndex() {
        createVersion4DatabaseWithDuplicateCachedMovies()

        val database = Room.databaseBuilder(context, CinemaPulseDatabase::class.java, TEST_DB)
            .addMigrations(*CinemaPulseMigrations.ALL)
            .build()
        try {
            val sqliteDatabase = database.openHelper.writableDatabase

            assertEquals(1, countCachedMovieRows(sqliteDatabase, movieId = 1))
            assertEquals("Inception Updated", findCachedMovieTitle(sqliteDatabase, movieId = 1))
            assertDuplicateInsertFails(sqliteDatabase)
        } finally {
            database.close()
        }
    }

    private fun createVersion4DatabaseWithDuplicateCachedMovies() {
        context.deleteDatabase(TEST_DB)
        val database = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(TEST_DB), null)
        database.use {
            it.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `movies` (
                    `rowId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `id` INTEGER NOT NULL,
                    `category` TEXT NOT NULL,
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
                    `page` INTEGER NOT NULL,
                    `totalPages` INTEGER NOT NULL,
                    `cachedAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            it.execSQL(
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
            it.execSQL(
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

            it.insert("movies", null, cachedMovieValues(rowId = 1, title = "Inception"))
            it.insert("movies", null, cachedMovieValues(rowId = 2, title = "Inception Updated"))
            it.insert("movies", null, cachedMovieValues(rowId = 3, movieId = 2, title = "Interstellar"))
            it.version = 4
        }
    }

    private fun cachedMovieValues(
        rowId: Int,
        movieId: Int = 1,
        title: String
    ): ContentValues = ContentValues().apply {
        put("rowId", rowId)
        put("id", movieId)
        put("category", "POPULAR")
        put("title", title)
        put("overview", "Overview")
        put("popularity", 10.0)
        put("releaseDate", "2010-07-16")
        put("voteAverage", 8.8)
        put("voteCount", 1000)
        putNull("posterPath")
        putNull("backdropPath")
        put("genreIds", "28")
        put("adult", 0)
        put("originalLanguage", "en")
        put("originalTitle", title)
        put("video", 0)
        put("page", 1)
        put("totalPages", 5)
        put("cachedAt", rowId.toLong())
    }

    private fun countCachedMovieRows(database: androidx.sqlite.db.SupportSQLiteDatabase, movieId: Int): Int =
        database.query(
            "SELECT COUNT(*) FROM `movies` WHERE `category` = 'POPULAR' AND `page` = 1 AND `id` = $movieId"
        ).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

    private fun findCachedMovieTitle(database: androidx.sqlite.db.SupportSQLiteDatabase, movieId: Int): String =
        database.query(
            "SELECT `title` FROM `movies` WHERE `category` = 'POPULAR' AND `page` = 1 AND `id` = $movieId"
        ).use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)
        }

    private fun assertDuplicateInsertFails(database: androidx.sqlite.db.SupportSQLiteDatabase) {
        assertThrows(SQLiteConstraintException::class.java) {
            database.execSQL(
                """
                INSERT INTO `movies` (
                    `id`, `category`, `title`, `overview`, `popularity`, `releaseDate`,
                    `voteAverage`, `voteCount`, `posterPath`, `backdropPath`, `genreIds`,
                    `adult`, `originalLanguage`, `originalTitle`, `video`, `page`,
                    `totalPages`, `cachedAt`
                ) VALUES (
                    1, 'POPULAR', 'Duplicate', 'Overview', 10.0, '2010-07-16',
                    8.8, 1000, NULL, NULL, '28',
                    0, 'en', 'Duplicate', 0, 1,
                    5, 99
                )
                """.trimIndent()
            )
        }
    }

    private companion object {
        const val TEST_DB = "cinema-pulse-migration-test.db"
    }
}
